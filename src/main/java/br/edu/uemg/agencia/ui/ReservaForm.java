package br.edu.uemg.agencia.ui;

import br.edu.uemg.agencia.auth.PermissionUtil;
import br.edu.uemg.agencia.modelo.Cliente;
import br.edu.uemg.agencia.modelo.Pacote;
import br.edu.uemg.agencia.modelo.Reserva;
import br.edu.uemg.agencia.pagamento.PagamentoFactory;
import br.edu.uemg.agencia.pagamento.Pagavel;
import br.edu.uemg.agencia.repos.ClienteRepo;
import br.edu.uemg.agencia.repos.PacoteRepo;
import br.edu.uemg.agencia.repos.ReservaRepo;
import br.edu.uemg.agencia.servico.ReservaService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ReservaForm extends JFrame {

    private final ReservaService service = new ReservaService();
    private final ClienteRepo clienteRepo = new ClienteRepo();
    private final PacoteRepo pacoteRepo = new PacoteRepo();

    private JComboBox<Cliente> cbCliente;
    private JComboBox<Pacote> cbPacote;
    private JLabel lblTotal;
    private JRadioButton rbPix, rbCartao;
    private DefaultTableModel tableModel;
    private JTable table;

    public ReservaForm() {
        setTitle("Central de Reservas");
        setSize(1100, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initUI();
        loadCombos();
        loadTable();
    }

    private void initUI() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(ModernUI.getBgColor());

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(ModernUI.getSurfaceColor());
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0,0,1,0, ModernUI.getBorderColor()),
                new EmptyBorder(20, 40, 20, 40)
        ));
        JLabel title = new JLabel("Reservas & Vendas");
        title.setFont(ModernUI.FONT_HEADER);
        title.setForeground(ModernUI.getTextColor());
        header.add(title);
        main.add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 25));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel simCard = ModernUI.createCard();
        simCard.setLayout(new BorderLayout(20, 0));

        JPanel inputs = new JPanel(new GridLayout(3, 1, 0, 15));
        inputs.setBackground(ModernUI.getSurfaceColor());

        cbCliente = new JComboBox<>(); styleCombo(cbCliente);
        cbPacote = new JComboBox<>(); styleCombo(cbPacote);

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioPanel.setBackground(ModernUI.getSurfaceColor());

        rbPix = new JRadioButton("PIX");
        rbPix.setBackground(ModernUI.getSurfaceColor());
        rbPix.setForeground(ModernUI.getTextColor());
        rbPix.setSelected(true);

        rbCartao = new JRadioButton("Cartão (+2.5%)");
        rbCartao.setBackground(ModernUI.getSurfaceColor());
        rbCartao.setForeground(ModernUI.getTextColor());

        ButtonGroup bg = new ButtonGroup(); bg.add(rbPix); bg.add(rbCartao);
        radioPanel.add(rbPix); radioPanel.add(rbCartao);

        inputs.add(ModernUI.createFieldGroup("1. Cliente", cbCliente));
        inputs.add(ModernUI.createFieldGroup("2. Pacote", cbPacote));
        inputs.add(ModernUI.createFieldGroup("3. Pagamento", radioPanel));

        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBackground(new Color(248, 250, 252));
        totalPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        JLabel lblTotalTitle = new JLabel("TOTAL ESTIMADO");
        lblTotalTitle.setFont(ModernUI.FONT_BOLD);
        lblTotalTitle.setForeground(ModernUI.getTextGrayColor());

        lblTotal = new JLabel("R$ 0,00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTotal.setForeground(ModernUI.COL_PRIMARY_1);

        JButton btnCriar = ModernUI.createButton("Confirmar Venda", true);
        btnCriar.addActionListener(e -> onCriar());

        totalPanel.add(lblTotalTitle, BorderLayout.NORTH);
        totalPanel.add(lblTotal, BorderLayout.CENTER);
        totalPanel.add(btnCriar, BorderLayout.SOUTH);

        simCard.add(inputs, BorderLayout.CENTER);
        simCard.add(totalPanel, BorderLayout.EAST);

        JPanel listCard = ModernUI.createCard();
        listCard.setLayout(new BorderLayout());

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setBackground(ModernUI.getSurfaceColor());

        JButton btnConfirmar = ModernUI.createFlatButton("✔ Confirmar Pagto", ModernUI.COL_ACCENT_SUCCESS);
        JButton btnCancelar = ModernUI.createFlatButton("✖ Cancelar", ModernUI.COL_ACCENT_DANGER);
        JButton btnExport = ModernUI.createFlatButton("📄 Recibo", ModernUI.COL_PRIMARY_1);

        btnConfirmar.addActionListener(e -> onConfirmarPagto());
        btnCancelar.addActionListener(e -> onCancelar());
        btnExport.addActionListener(e -> onExportar());

        toolbar.add(btnConfirmar); toolbar.add(btnExport); toolbar.add(btnCancelar);

        tableModel = new DefaultTableModel(new Object[]{"ID", "Cliente", "Pacote", "Status", "Valor"}, 0);
        table = new JTable(tableModel);
        ModernUI.styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(ModernUI.getSurfaceColor());

        listCard.add(toolbar, BorderLayout.NORTH);
        listCard.add(scroll, BorderLayout.CENTER);

        content.add(simCard, BorderLayout.NORTH);
        content.add(listCard, BorderLayout.CENTER);
        main.add(content, BorderLayout.CENTER);
        setContentPane(main);

        cbPacote.addActionListener(e -> updateSimulacao());
        rbPix.addActionListener(e -> updateSimulacao());
        rbCartao.addActionListener(e -> updateSimulacao());
    }

    private void styleCombo(JComboBox box) {
        box.setFont(ModernUI.FONT_BODY);
        box.setBackground(ModernUI.getSurfaceColor());
        box.setForeground(ModernUI.getTextColor());
        box.setBorder(BorderFactory.createLineBorder(ModernUI.getBorderColor()));
    }

    private void updateSimulacao() {
        Pacote p = (Pacote) cbPacote.getSelectedItem();

        if(p != null) {
            String tipoSelecionado = rbPix.isSelected() ? "PIX" : "CARTAO";
            Pagavel estrategia = PagamentoFactory.criar(tipoSelecionado);
            double valFinal = estrategia.calcularValorFinal(p.getValorBase());
            lblTotal.setText(String.format("R$ %.2f", valFinal));
        }
    }
    private void onCriar() {
        try {
            Reserva r = new Reserva();
            r.setCliente((Cliente) cbCliente.getSelectedItem());
            r.setPacote((Pacote) cbPacote.getSelectedItem());
            service.criarReserva(r);
            loadTable();
            JOptionPane.showMessageDialog(this, "Venda registrada!");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }
    private void onConfirmarPagto() {
        if(!PermissionUtil.requireAdmin(this, "Confirmar Pagamento", "ReservaForm")) return;
        int row = table.getSelectedRow();
        if(row < 0) return;
        try {
            int id = (int) tableModel.getValueAt(row, 0);
            Optional<Reserva> r = new ReservaRepo().findById(id);
            if(r.isPresent()) {
                String metodo = JOptionPane.showOptionDialog(this, "Confirma o recebimento?", "Pagamento", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"PIX", "Cartão"}, "PIX") == 1 ? "cartao" : "pix";
                service.confirmarPagamento(r.get(), metodo);
                loadTable();
                JOptionPane.showMessageDialog(this, "Pagamento Confirmado!");
            }
        } catch(Exception e) { e.printStackTrace(); }
    }
    private void onExportar() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma reserva");
            return;
        }

        try {
            int id = (int) tableModel.getValueAt(row, 0);
            Optional<Reserva> r = new ReservaRepo().findById(id);

            if (r.isPresent()) {
                JFileChooser fc = new JFileChooser();
                fc.setSelectedFile(new File("Recibo_Reserva_" + id + ".txt"));

                if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    try (FileWriter fw = new FileWriter(fc.getSelectedFile())) {
                        fw.write(gerarRecibo(r.get()));
                    }
                    JOptionPane.showMessageDialog(this, "Arquivo salvo!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onCancelar() {
        if(!PermissionUtil.requireAdmin(this, "Cancelar", "ReservaForm")) return;
        int row = table.getSelectedRow();
        if(row >= 0) {
            int id = (int) tableModel.getValueAt(row, 0);
            new ReservaRepo().findById(id).ifPresent(service::cancelarReserva);
            loadTable();
        }
    }
    private String gerarRecibo(Reserva r) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return "RECIBO AGENCIA\nReserva: " + r.getId() + "\nData: " + r.getDataReserva().format(f) + "\nCliente: " + r.getCliente().getNome() + "\nValor: " + r.getValorTotal();
    }
    private void loadCombos() {
        cbCliente.removeAllItems(); clienteRepo.findAll().forEach(cbCliente::addItem);
        cbPacote.removeAllItems(); pacoteRepo.findAll().forEach(cbPacote::addItem);

        cbCliente.setRenderer((l,v,i,s,f) -> {
            JLabel lbl = new JLabel(v!=null?v.getNome():"");
            lbl.setOpaque(true);
            lbl.setBackground(s ? ModernUI.COL_PRIMARY_1 : ModernUI.getSurfaceColor());
            lbl.setForeground(s ? Color.WHITE : ModernUI.getTextColor());
            lbl.setBorder(new EmptyBorder(5,10,5,10));
            return lbl;
        });

        cbPacote.setRenderer((l,v,i,s,f) -> {
            JLabel lbl = new JLabel(v!=null?v.getDestino()+" (R$ "+v.getValorBase()+")":"");
            lbl.setOpaque(true);
            lbl.setBackground(s ? ModernUI.COL_PRIMARY_1 : ModernUI.getSurfaceColor());
            lbl.setForeground(s ? Color.WHITE : ModernUI.getTextColor());
            lbl.setBorder(new EmptyBorder(5,10,5,10));
            return lbl;
        });

        ComboSearchSupport.enable(cbCliente, clienteRepo.findAll(), Cliente::getNome);
        ComboSearchSupport.enable(cbPacote, pacoteRepo.findAll(), Pacote::getDestino);
    }
    private void loadTable() {
        tableModel.setRowCount(0);
        service.listarTodas().forEach(r -> tableModel.addRow(new Object[]{
                r.getId(),
                r.getCliente().getNome(),
                r.getPacote().getDestino(),
                r.getStatus(),
                String.format("R$ %.2f", r.getValorTotal())
        }));
    }


}