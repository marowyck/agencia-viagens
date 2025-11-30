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

public class ReservaForm extends JFrame {
    private final ReservaService service = new ReservaService();
    private JComboBox<Cliente> cbCliente;
    private JComboBox<Pacote> cbPacote;
    private JFormattedTextField tfData;
    private JLabel lblTotal, lblRetorno;
    private JRadioButton rbPix, rbCartao;
    private DefaultTableModel tableModel;
    private JTable table;

    public ReservaForm() {
        ModernUI.setupTheme(this);
        setTitle("Vendas");
        setSize(1100, 800);
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

        JPanel inputs = new JPanel(new GridLayout(4, 1, 10, 10));
        inputs.setOpaque(false);

        cbCliente = new JComboBox<>();
        cbPacote = new JComboBox<>();
        tfData = FormatUtil.createFormattedField("##/##/####");
        tfData.setFont(ModernUI.FONT_PLAIN);

        JPanel pay = new JPanel(new FlowLayout(FlowLayout.LEFT)); pay.setOpaque(false);
        rbPix = new JRadioButton("PIX"); rbPix.setForeground(ModernUI.COL_TEXT_H1); rbPix.setSelected(true); rbPix.setOpaque(false);
        rbCartao = new JRadioButton("Cartão"); rbCartao.setForeground(ModernUI.COL_TEXT_H1); rbCartao.setOpaque(false);
        ButtonGroup bg = new ButtonGroup(); bg.add(rbPix); bg.add(rbCartao);
        pay.add(rbPix); pay.add(rbCartao);

        inputs.add(ModernUI.createLabelGroup("Cliente", cbCliente));
        inputs.add(ModernUI.createLabelGroup("Pacote", cbPacote));
        inputs.add(ModernUI.createLabelGroup("Data Ida", tfData));
        inputs.add(ModernUI.createLabelGroup("Pagamento", pay));

        JPanel totalP = new JPanel(new BorderLayout());
        totalP.setOpaque(false);
        totalP.setBorder(new EmptyBorder(10,30,10,30));
        JLabel lTot = new JLabel("TOTAL"); lTot.setForeground(ModernUI.COL_TEXT_BODY);
        lblTotal = new JLabel("R$ 0,00"); lblTotal.setFont(new Font("SansSerif", Font.BOLD, 36));
        lblTotal.setForeground(ModernUI.BRAND);
        lblRetorno = new JLabel("Retorno: -"); lblRetorno.setForeground(ModernUI.COL_TEXT_BODY);

        totalP.add(lTot, BorderLayout.NORTH);
        totalP.add(lblTotal, BorderLayout.CENTER);
        totalP.add(lblRetorno, BorderLayout.SOUTH);

        JButton btnConfirm = ModernUI.createButton("CONFIRMAR VENDA");
        btnConfirm.setPreferredSize(new Dimension(200, 50));
        btnConfirm.addActionListener(e -> save());

        form.add(inputs, BorderLayout.CENTER);
        form.add(totalP, BorderLayout.EAST);
        form.add(btnConfirm, BorderLayout.SOUTH);

        cbPacote.addActionListener(e -> calc());
        rbPix.addActionListener(e -> calc());
        rbCartao.addActionListener(e -> calc());
        tfData.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { if(tfData.getText().length()==10) calc(); }
        });

        JPanel list = ModernUI.createCard();
        list.setLayout(new BorderLayout());
        JPanel tools = new JPanel(new FlowLayout(FlowLayout.LEFT)); tools.setOpaque(false);
        JButton btnPay = ModernUI.createOutlineButton("Confirmar Pagto");
        JButton btnHtml = ModernUI.createOutlineButton("HTML Recibo");
        JButton btnCancel = ModernUI.createButton("Cancelar"); btnCancel.setBackground(ModernUI.DANGER);

        btnPay.addActionListener(e -> confirmPay());
        btnHtml.addActionListener(e -> exportHtml());
        btnCancel.addActionListener(e -> cancel());

        tools.add(btnPay); tools.add(btnHtml); tools.add(btnCancel);
        list.add(tools, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"ID", "Cliente", "Pacote", "Status", "Valor"},0);
        table = new JTable(tableModel);
        ModernUI.styleTable(table);
        list.add(new JScrollPane(table), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, form, list);
        split.setOpaque(false); split.setBorder(null); split.setDividerLocation(350);
        main.add(split, BorderLayout.CENTER);
        setContentPane(main);
    }

    private void load() {
        cbCliente.removeAllItems(); new ClienteRepo().findAll().forEach(cbCliente::addItem);
        cbPacote.removeAllItems(); new PacoteRepo().findAll().forEach(cbPacote::addItem);
        ComboSearchSupport.enable(cbCliente, new ClienteRepo().findAll(), Cliente::getNome);
        ComboSearchSupport.enable(cbPacote, new PacoteRepo().findAll(), Pacote::getDestino);

        tableModel.setRowCount(0);
        service.listarTodas().forEach(r -> tableModel.addRow(new Object[]{r.getId(), r.getCliente().getNome(), r.getPacote().getDestino(), r.getStatus(), r.getValorTotal()}));
    }

    private void calc() {
        try {
            Pacote p = (Pacote) cbPacote.getSelectedItem();
            LocalDate d = LocalDate.parse(tfData.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            if(p!=null) {
                lblRetorno.setText("Retorno: " + d.plusDays(p.getDuracao()));
                double val = service.simularValorFinal(p, rbCartao.isSelected(), d);
                lblTotal.setText(String.format("R$ %.2f", val));
            }
        } catch(Exception e) { lblRetorno.setText("Data inv."); }
    }

    private void save() {
        try {
            Reserva r = new Reserva();
            r.setCliente((Cliente)cbCliente.getSelectedItem());
            r.setPacote((Pacote)cbPacote.getSelectedItem());
            r.setValorTotal(Double.parseDouble(lblTotal.getText().replace("R$","").replace(",",". நிற்கும்").trim()));
            service.criarReserva(r);
            load();
            JOptionPane.showMessageDialog(this, "Sucesso!");
        } catch(Exception e) { JOptionPane.showMessageDialog(this, "Erro: "+e.getMessage()); }
    }

    private void confirmPay() {
        if(!PermissionUtil.requireAdmin(this, "Pagto", "Reserva")) return;
        int r = table.getSelectedRow();
        if(r>=0) try {
            int id = (int)tableModel.getValueAt(r,0);
            service.confirmarPagamento(new ReservaRepo().findById(id).get(), rbPix.isSelected()?"pix":"cartao");
            load();
        } catch(Exception e) { e.printStackTrace(); }
    }

    private void cancel() {
        if(!PermissionUtil.requireAdmin(this, "Cancel", "Reserva")) return;
        int r = table.getSelectedRow();
        if(r>=0) {
            new ReservaRepo().findById((int)tableModel.getValueAt(r,0)).ifPresent(service::cancelarReserva);
            load();
        }
    }

    private void exportHtml() {
        int r = table.getSelectedRow();
        if(r<0) return;
        try {
            int id = (int)tableModel.getValueAt(r,0);
            Reserva res = new ReservaRepo().findById(id).orElse(null);
            File f = new File("recibo.html");
            FileWriter fw = new FileWriter(f);
            fw.write("<h1>Recibo #"+id+"</h1><p>Cliente: "+res.getCliente().getNome()+"</p><h2>Valor: R$ "+res.getValorTotal()+"</h2>");
            fw.close();
            ExternalService.abrirHtml(f);
        } catch(Exception e) {}
    }
}