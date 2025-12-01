package br.edu.uemg.agencia.ui;

import br.edu.uemg.agencia.auth.PermissionUtil;
import br.edu.uemg.agencia.modelo.*;
import br.edu.uemg.agencia.repos.*;
import br.edu.uemg.agencia.servico.ReservaService;
import br.edu.uemg.agencia.util.ExternalService;
import br.edu.uemg.agencia.util.FormatUtil;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReservaForm extends JFrame {
    private final ReservaService service = new ReservaService();
    private JComboBox<Cliente> cbCliente;
    private JComboBox<Pacote> cbPacote;
    private JComboBox<String> cbParcelas;

    private JFormattedTextField tfData;
    private JLabel lblTotal, lblRetorno, lblMsgJuros;
    private JRadioButton rbPix, rbCartao;
    private DefaultTableModel tableModel;
    private JTable table;

    private double valorRealCalculado = 0.0;

    public ReservaForm() {
        ModernUI.setupTheme(this);
        setTitle("Vendas e Reservas");
        setSize(1150, 850);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
        load();
    }

    private void initUI() {
        JPanel main = new JPanel(new BorderLayout(20,20));
        main.setBackground(ModernUI.COL_BG);
        main.setBorder(new EmptyBorder(20,20,20,20));

        JPanel form = ModernUI.createCard();
        form.setLayout(new BorderLayout(20,0));

        JPanel inputs = new JPanel(new GridLayout(5, 1, 10, 10));
        inputs.setOpaque(false);

        cbCliente = new JComboBox<>();
        cbPacote = new JComboBox<>();
        tfData = FormatUtil.createFormattedField("##/##/####");
        tfData.setFont(ModernUI.FONT_PLAIN);
        tfData.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        JPanel pay = new JPanel(new FlowLayout(FlowLayout.LEFT)); pay.setOpaque(false);
        rbPix = new JRadioButton("PIX (-5%)"); rbPix.setForeground(ModernUI.COL_TEXT_H1); rbPix.setSelected(true); rbPix.setOpaque(false);
        rbCartao = new JRadioButton("Cartão"); rbCartao.setForeground(ModernUI.COL_TEXT_H1); rbCartao.setOpaque(false);
        ButtonGroup bg = new ButtonGroup(); bg.add(rbPix); bg.add(rbCartao);
        pay.add(rbPix); pay.add(rbCartao);

        cbParcelas = new JComboBox<>();
        cbParcelas.addItem("1x (À vista)");
        for(int i=2; i<=12; i++) cbParcelas.addItem(i + "x");
        cbParcelas.setEnabled(false);

        inputs.add(ModernUI.createLabelGroup("Cliente", cbCliente));
        inputs.add(ModernUI.createLabelGroup("Pacote", cbPacote));
        inputs.add(ModernUI.createLabelGroup("Data Ida", tfData));
        inputs.add(ModernUI.createLabelGroup("Método", pay));
        inputs.add(ModernUI.createLabelGroup("Parcelamento", cbParcelas));

        JPanel totalP = new JPanel(new BorderLayout());
        totalP.setOpaque(false);
        totalP.setBorder(new EmptyBorder(10,30,10,30));

        JLabel lTot = new JLabel("TOTAL FINAL"); lTot.setForeground(ModernUI.COL_TEXT_BODY);
        lblTotal = new JLabel("R$ 0,00"); lblTotal.setFont(new Font("SansSerif", Font.BOLD, 36));
        lblTotal.setForeground(ModernUI.BRAND);

        lblRetorno = new JLabel("Retorno: -"); lblRetorno.setForeground(ModernUI.COL_TEXT_BODY);
        lblMsgJuros = new JLabel(""); lblMsgJuros.setFont(new Font("SansSerif", Font.PLAIN, 11));

        JPanel infoPanel = new JPanel(new GridLayout(3,1)); infoPanel.setOpaque(false);
        infoPanel.add(lblRetorno);
        infoPanel.add(lblMsgJuros);

        totalP.add(lTot, BorderLayout.NORTH);
        totalP.add(lblTotal, BorderLayout.CENTER);
        totalP.add(infoPanel, BorderLayout.SOUTH);

        JButton btnConfirm = ModernUI.createButton("CONFIRMAR VENDA");
        btnConfirm.setPreferredSize(new Dimension(200, 50));
        btnConfirm.addActionListener(e -> save());

        form.add(inputs, BorderLayout.CENTER);
        form.add(totalP, BorderLayout.EAST);
        form.add(btnConfirm, BorderLayout.SOUTH);

        cbPacote.addActionListener(e -> calc());
        rbPix.addActionListener(e -> { cbParcelas.setEnabled(false); cbParcelas.setSelectedIndex(0); calc(); });
        rbCartao.addActionListener(e -> { cbParcelas.setEnabled(true); calc(); });
        cbParcelas.addActionListener(e -> calc());
        tfData.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { calc(); }
        });

        JPanel list = ModernUI.createCard();
        list.setLayout(new BorderLayout());
        JPanel tools = new JPanel(new FlowLayout(FlowLayout.LEFT)); tools.setOpaque(false);
        JButton btnPay = ModernUI.createOutlineButton("Confirmar Pagto");
        JButton btnRes = ModernUI.createOutlineButton("📅 Reagendar");
        JButton btnHtml = ModernUI.createOutlineButton("📄 Recibo / Estorno");
        JButton btnCancel = ModernUI.createButton("Cancelar"); btnCancel.setBackground(ModernUI.DANGER);

        btnPay.addActionListener(e -> confirmPay());
        btnRes.addActionListener(e -> showRescheduleDialog());
        btnHtml.addActionListener(e -> exportHtml());
        btnCancel.addActionListener(e -> cancel());

        tools.add(btnPay); tools.add(btnRes); tools.add(btnHtml); tools.add(btnCancel);
        list.add(tools, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"ID", "Cliente", "Pacote", "Status", "Valor"},0);
        table = new JTable(tableModel);
        ModernUI.styleTable(table);
        list.add(new JScrollPane(table), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, form, list);
        split.setOpaque(false); split.setBorder(null); split.setDividerLocation(380);
        main.add(split, BorderLayout.CENTER);
        setContentPane(main);
    }

    private void calc() {
        Pacote p = (Pacote) cbPacote.getSelectedItem();
        if(p == null) return;

        LocalDate dataViagem = null;
        try {
            dataViagem = LocalDate.parse(tfData.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            lblRetorno.setText("Retorno: " + dataViagem.plusDays(p.getDuracao()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        } catch(Exception e) {
            lblRetorno.setText("Data incompleta...");
            dataViagem = null;
        }

        boolean isCartao = rbCartao.isSelected();
        int parcelas = cbParcelas.getSelectedIndex() + 1;

        valorRealCalculado = service.simularValorFinal(p, isCartao, parcelas, dataViagem);
        lblTotal.setText(String.format("R$ %.2f", valorRealCalculado));

        if(!isCartao) {
            lblMsgJuros.setText("Desconto PIX aplicado (-5%)");
            lblMsgJuros.setForeground(ModernUI.SUCCESS);
        } else {
            if(parcelas > 3) {
                lblMsgJuros.setText("Juros aplicados (1.99% a.m.)");
                lblMsgJuros.setForeground(ModernUI.DANGER);
            } else {
                lblMsgJuros.setText("Sem juros (1x-3x)");
                lblMsgJuros.setForeground(ModernUI.COL_TEXT_BODY);
            }
            double valParcela = valorRealCalculado / parcelas;
            lblTotal.setToolTipText(parcelas + "x de R$ " + String.format("%.2f", valParcela));
        }
    }

    private void save() {
        try {
            Reserva r = new Reserva();
            r.setCliente((Cliente)cbCliente.getSelectedItem());
            r.setPacote((Pacote)cbPacote.getSelectedItem());
            r.setValorTotal(valorRealCalculado);
            try {
                LocalDate d = LocalDate.parse(tfData.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                r.setDataReserva(d.atStartOfDay());
            } catch (Exception ex) { throw new IllegalArgumentException("Data inválida."); }
            service.criarReserva(r);
            load(); JOptionPane.showMessageDialog(this, "Venda Realizada!");
        } catch(Exception e) { JOptionPane.showMessageDialog(this, "Erro: "+e.getMessage()); }
    }

    private void confirmPay() {
        if(!PermissionUtil.requireAdmin(this, "Pagto", "Reserva")) return;
        int r = table.getSelectedRow();
        if(r < 0) return;

        try {
            int id = (int)tableModel.getValueAt(r,0);
            Optional<Reserva> res = new ReservaRepo().findById(id);
            if(res.isPresent()) {
                String metodo = "PIX";
                if(JOptionPane.showConfirmDialog(this, "O pagamento foi em CARTÃO?", "Método", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    String parcelasStr = JOptionPane.showInputDialog("Quantidade de Parcelas (1-12):", "1");
                    metodo = "Cartão (" + parcelasStr + "x)";
                }
                service.confirmarPagamento(res.get(), metodo);
                load(); JOptionPane.showMessageDialog(this, "Pagamento Confirmado!");
            }
        } catch(Exception e) { e.printStackTrace(); }
    }

    private void cancel() {
        if(!PermissionUtil.requireAdmin(this, "Cancel", "Reserva")) return;
        int r = table.getSelectedRow();
        if(r>=0) {
            new ReservaRepo().findById((int)tableModel.getValueAt(r,0)).ifPresent(res -> {
                String msg = service.cancelarReserva(res);
                load(); JOptionPane.showMessageDialog(this, msg);
            });
        }
    }

    private void exportHtml() {
        int r = table.getSelectedRow();
        if(r < 0) { JOptionPane.showMessageDialog(this, "Selecione uma reserva."); return; }

        try {
            int id = (int)tableModel.getValueAt(r,0);
            ReservaRepo repo = new ReservaRepo();
            Reserva res = repo.findById(id).orElse(null);
            if(res == null) return;

            String metodoPagamento = repo.getPagamentoMetodo(id);
            if(metodoPagamento == null) metodoPagamento = "Pendente";

            String infoParcelas = "";
            if (metodoPagamento.toLowerCase().contains("cartão") && metodoPagamento.contains("x")) {
                try {
                    Pattern p = Pattern.compile("\\((\\d+)x\\)");
                    Matcher m = p.matcher(metodoPagamento);
                    if(m.find()) {
                        int numParcelas = Integer.parseInt(m.group(1));
                        double valParcela = res.getValorTotal() / numParcelas;
                        infoParcelas = String.format("<p style='color:#003580; font-weight:bold; margin:5px 0;'>Parcelamento: %dx de R$ %.2f</p>", numParcelas, valParcela);
                    }
                } catch(Exception ignored) {}
            }

            String statusColor = "#008009";
            String extraInfo = "";

            if("Cancelada".equalsIgnoreCase(res.getStatus())) {
                statusColor = "#D4111E";
                LocalDate dataViagem = res.getDataReserva().toLocalDate();

                long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), dataViagem);

                double valorPago = res.getValorTotal();
                double multa = 0.0;
                double reembolso = valorPago;

                String motivoMulta = "Cancelamento Antecipado (Sem multa)";
                String boxColor = "#F0FFF4";
                String borderColor = "#48BB78";
                String titleColor = "#2F855A";

                if (diasRestantes < 0) {
                    motivoMulta = "Viagem já realizada (Sem reembolso)";
                    reembolso = 0.0;
                    boxColor = "#EDF2F7"; borderColor = "#CBD5E0"; titleColor = "#4A5568";
                } else if (diasRestantes <= 7) {
                    multa = valorPago * 0.20;
                    reembolso = valorPago - multa;
                    motivoMulta = "Cancelamento Tardio (< 7 dias para viagem)";
                    boxColor = "#FFF5F5"; borderColor = "#FEB2B2"; titleColor = "#C53030";
                }

                extraInfo = """
                    <div style='background:%s; border:1px solid %s; padding:15px; border-radius:5px; margin-top:20px;'>
                        <h3 style='color:%s; margin-top:0;'>🛑 Comprovante de Estorno</h3>
                        <p><b>Motivo:</b> %s</p>
                        <p><b>Dias para a viagem:</b> %d dias</p>
                        <p><b>Valor Original:</b> R$ %.2f</p>
                        <p><b>Taxa de Cancelamento:</b> - R$ %.2f</p>
                        <hr style='border-color:%s'>
                        <h2 style='color:%s; text-align:right;'>A Restituir: R$ %.2f</h2>
                    </div>
                """.formatted(boxColor, borderColor, titleColor, motivoMulta, diasRestantes, valorPago, multa, borderColor, titleColor, reembolso);
            }

            File f = new File("recibo_" + id + ".html");
            FileWriter fw = new FileWriter(f);

            String html = """
                <html>
                <body style='font-family:sans-serif; padding:40px; background:#f0f2f5;'>
                    <div style='background:white; padding:40px; border-radius:10px; max-width:600px; margin:auto; box-shadow:0 4px 15px rgba(0,0,0,0.1);'>
                        <div style='text-align:center; border-bottom:2px solid #eee; padding-bottom:20px; margin-bottom:20px;'>
                            <h1 style='color:#003580; margin:0;'>AGÊNCIA VIAGENS++</h1>
                            <p style='color:#777; margin:5px;'>Comprovante Oficial #%d</p>
                        </div>
                        
                        <table style='width:100%%; border-collapse:collapse;'>
                            <tr><td style='padding:8px; color:#555;'>Cliente:</td><td style='font-weight:bold;'>%s</td></tr>
                            <tr><td style='padding:8px; color:#555;'>Pacote:</td><td style='font-weight:bold;'>%s</td></tr>
                            <tr><td style='padding:8px; color:#555;'>Data Viagem:</td><td style='font-weight:bold;'>%s</td></tr>
                            <tr><td style='padding:8px; color:#555;'>Método:</td><td style='font-weight:bold;'>%s</td></tr>
                            <tr><td style='padding:8px; color:#555;'>Status:</td><td style='font-weight:bold; color:%s;'>%s</td></tr>
                        </table>
                        
                        <div style='text-align:right; margin-top:20px;'>
                            <p style='margin:0; font-size:14px; color:#777;'>Valor da Transação</p>
                            <h2 style='margin:5px 0; color:#333;'>R$ %.2f</h2>
                            %s
                        </div>
                        
                        %s 
                        
                        <p style='text-align:center; font-size:12px; color:#aaa; margin-top:40px;'>Documento gerado eletronicamente em %s</p>
                    </div>
                </body></html>
                """.formatted(
                    res.getId(),
                    res.getCliente().getNome(),
                    res.getPacote().getDestino(),
                    res.getDataReserva().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    metodoPagamento,
                    statusColor, res.getStatus().toUpperCase(),
                    res.getValorTotal(),
                    infoParcelas,
                    extraInfo,
                    LocalDate.now()
            );

            fw.write(html);
            fw.close();
            ExternalService.abrirHtml(f);

        } catch(Exception e) { e.printStackTrace(); }
    }

    private void showRescheduleDialog() {
        int r = table.getSelectedRow();
        if(r < 0) return;
        int id = (int) tableModel.getValueAt(r, 0);
        String input = JOptionPane.showInputDialog(this, "Nova Data (dd/MM/yyyy):");
        if(input != null) {
            try {
                LocalDate d = LocalDate.parse(input, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                service.reagendarReserva(id, d);
                load();
            } catch(Exception e) { JOptionPane.showMessageDialog(this, "Erro data"); }
        }
    }

    private void load() {
        cbCliente.removeAllItems(); new ClienteRepo().findAll().forEach(cbCliente::addItem);
        cbPacote.removeAllItems(); new PacoteRepo().findAll().forEach(cbPacote::addItem);
        ComboSearchSupport.enable(cbCliente, new ClienteRepo().findAll(), Cliente::getNome);
        ComboSearchSupport.enable(cbPacote, new PacoteRepo().findAll(), Pacote::getDestino);
        tableModel.setRowCount(0);
        service.listarTodas().forEach(r -> tableModel.addRow(new Object[]{r.getId(), r.getCliente().getNome(), r.getPacote().getDestino(), r.getStatus(), String.format("R$ %.2f", r.getValorTotal())}));
        calc();
    }
}