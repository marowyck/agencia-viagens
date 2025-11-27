package br.edu.uemg.agencia.ui;

import br.edu.uemg.agencia.modelo.Cliente;
import br.edu.uemg.agencia.modelo.Pacote;
import br.edu.uemg.agencia.modelo.Reserva;
import br.edu.uemg.agencia.repos.ClienteRepo;
import br.edu.uemg.agencia.repos.PacoteRepo;
import br.edu.uemg.agencia.repos.ReservaRepo;
import br.edu.uemg.agencia.servico.ReservaService;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class ReservaForm extends JFrame {

    private final ClienteRepo clienteRepo = new ClienteRepo();
    private final PacoteRepo pacoteRepo = new PacoteRepo();
    private final ReservaService service = new ReservaService();

    private final Color THEME_COLOR = new Color(16, 185, 129);

    private JComboBox<Cliente> cbCliente;
    private JComboBox<Pacote> cbPacote;
    private JLabel lblValorSimulado;
    private JRadioButton rbPix, rbCartao;

    private DefaultTableModel tableModel;
    private JTable table;

    public ReservaForm() {
        setTitle("Gestão de Reservas e Vendas");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
        loadCombos();
        loadTable();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(THEME_COLOR);
        headerPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel titleLabel = new JLabel("Reservas");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.putClientProperty(FlatClientProperties.STYLE, "font: bold +10");

        JLabel subtitleLabel = new JLabel("Simule valores, registre vendas e confirme pagamentos");
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));

        JPanel titleContainer = new JPanel(new GridLayout(2, 1));
        titleContainer.setOpaque(false);
        titleContainer.add(titleLabel);
        titleContainer.add(subtitleLabel);

        headerPanel.add(titleContainer, BorderLayout.WEST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 30, 15));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Nova Venda / Simulação"));

        cbCliente = new JComboBox<>();
        styleComboBox(cbCliente);

        cbPacote = new JComboBox<>();
        styleComboBox(cbPacote);

        JPanel pPagamento = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pPagamento.setBackground(Color.WHITE);
        rbPix = new JRadioButton("PIX (Padrão)");
        rbCartao = new JRadioButton("Cartão (+2.5%)");
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbPix); bg.add(rbCartao);
        rbPix.setSelected(true);
        pPagamento.add(rbPix);
        pPagamento.add(rbCartao);

        lblValorSimulado = new JLabel("R$ 0.00");
        lblValorSimulado.setForeground(THEME_COLOR);
        lblValorSimulado.putClientProperty(FlatClientProperties.STYLE, "font: bold +12");

        formPanel.add(createFieldContainer("1. Selecione o Cliente", cbCliente));
        formPanel.add(createFieldContainer("2. Selecione o Pacote", cbPacote));
        formPanel.add(createFieldContainer("3. Forma de Pagamento", pPagamento));
        formPanel.add(createFieldContainer("Total Estimado", lblValorSimulado));

        contentPanel.add(formPanel, BorderLayout.NORTH);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionPanel.setBackground(Color.WHITE);

        JButton btnCriar = createButton("Concluir Reserva", THEME_COLOR, Color.WHITE, true);

        JButton btnConfirmar = createButton("Confirmar Pagamento", new Color(59, 130, 246), Color.WHITE, true);
        JButton btnCancelar = createButton("Cancelar", new Color(245, 158, 11), Color.WHITE, true);
        JButton btnExcluir = createButton("Excluir", new Color(239, 68, 68), Color.WHITE, true);
        JButton btnRefresh = createButton("Recarregar", Color.WHITE, Color.GRAY, false);

        actionPanel.add(btnCriar);
        actionPanel.add(Box.createHorizontalStrut(20)); // Espaço divisor
        actionPanel.add(btnConfirmar);
        actionPanel.add(btnCancelar);
        actionPanel.add(btnExcluir);
        actionPanel.add(Box.createHorizontalStrut(20));
        actionPanel.add(btnRefresh);

        JPanel centerWrapper = new JPanel(new BorderLayout(0, 15));
        centerWrapper.setBackground(Color.WHITE);
        centerWrapper.add(actionPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"ID", "Cliente", "Pacote", "Data", "Status", "Valor Total"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        centerWrapper.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(centerWrapper, BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);

        btnCriar.addActionListener(e -> onCriar());
        btnConfirmar.addActionListener(e -> onConfirmar());
        btnCancelar.addActionListener(e -> onCancelar());
        btnExcluir.addActionListener(e -> onExcluir());
        btnRefresh.addActionListener(e -> loadTable());

        cbPacote.addActionListener(e -> onSimular());
        rbPix.addActionListener(e -> onSimular());
        rbCartao.addActionListener(e -> onSimular());
    }


    private void styleComboBox(JComboBox box) {
        box.putClientProperty(FlatClientProperties.STYLE, "arc: 10; padding: 5,10,5,10");
        box.setBackground(new Color(245, 245, 245));
    }

    private JPanel createFieldContainer(String labelText, JComponent input) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(labelText);
        lbl.putClientProperty(FlatClientProperties.STYLE, "font: bold small");
        lbl.setForeground(new Color(100, 100, 100));
        p.add(lbl, BorderLayout.NORTH);
        p.add(input, BorderLayout.CENTER);
        return p;
    }

    private JButton createButton(String text, Color bg, Color fg, boolean bold) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        String style = "arc: 10; margin: 8,20,8,20;";
        if(bold) style += "font: bold";
        btn.putClientProperty(FlatClientProperties.STYLE, style);
        return btn;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(16, 185, 129, 40)); // Verde claro na seleção
        table.setSelectionForeground(Color.BLACK);

        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer((table1, value, isSelected, hasFocus, row, column) -> {
            JLabel label = new JLabel(value.toString());
            label.setBackground(Color.WHITE);
            label.setForeground(new Color(100, 100, 100));
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, THEME_COLOR));
            label.setHorizontalAlignment(JLabel.LEFT);
            label.setBorder(BorderFactory.createCompoundBorder(
                    label.getBorder(), BorderFactory.createEmptyBorder(0, 5, 0, 0)));
            return label;
        });
    }


    private void loadCombos() {
        cbCliente.removeAllItems();
        List<Cliente> clientes = clienteRepo.findAll();
        for (Cliente c : clientes) cbCliente.addItem(c);

        cbPacote.removeAllItems();
        List<Pacote> pacotes = pacoteRepo.findAll();
        for (Pacote p : pacotes) cbPacote.addItem(p);

        cbCliente.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Cliente) {
                    setText(((Cliente) value).getNome());
                }
                return this;
            }
        });

        cbPacote.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Pacote) {
                    Pacote p = (Pacote) value;
                    setText(p.getDestino() + " - R$ " + p.getValorBase());
                }
                return this;
            }
        });
    }

    private void onSimular() {
        Pacote p = (Pacote) cbPacote.getSelectedItem();
        if (p == null) return;
        boolean cartao = rbCartao.isSelected();
        double valor = service.simularValorFinal(p, cartao);
        lblValorSimulado.setText(String.format("R$ %.2f", valor));
    }

    private void onCriar() {
        Cliente selCliente = (Cliente) cbCliente.getSelectedItem();
        Pacote selPacote = (Pacote) cbPacote.getSelectedItem();

        if (selCliente == null || selPacote == null) {
            JOptionPane.showMessageDialog(this, "Selecione cliente e pacote para continuar.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Reserva r = new Reserva();
        r.setCliente(selCliente);
        r.setPacote(selPacote);
        try {
            Reserva criada = service.criarReserva(r);
            JOptionPane.showMessageDialog(this, "Reserva criada com sucesso!\nID: " + criada.getId(), "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            loadTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao criar reserva: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onConfirmar() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma reserva na tabela para confirmar o pagamento.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
            ReservaRepo rr = new ReservaRepo();
            Optional<Reserva> opt = rr.findById(id);

            if (opt.isEmpty()) return;

            Reserva r = opt.get();
            String[] options = {"PIX", "Cartão"};
            int escolha = JOptionPane.showOptionDialog(this, "Qual foi o meio de pagamento efetivado?", "Confirmar Pagamento",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

            String metodo = (escolha == 1) ? "cartao" : "pix";

            service.confirmarPagamento(r, metodo);
            String recibo = gerarRecibo(r, metodo);

            JTextArea textArea = new JTextArea(recibo);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            textArea.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Recibo de Pagamento", JOptionPane.INFORMATION_MESSAGE);

            loadTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao processar pagamento: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String gerarRecibo(Reserva r, String metodo) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String dataF = r.getDataReserva().format(f);

        return String.format("""
                --------------------------------
                     AGÊNCIA VIAGENS++
                     Recibo de Pagamento
                --------------------------------
                Reserva ID: %d
                Data:       %s
                Cliente:    %s
                CPF:        %s
                --------------------------------
                Pacote:     %s
                Pagamento:  %s
                TOTAL:      R$ %.2f
                --------------------------------
                Obrigado pela preferência!
                """,
                r.getId(), dataF,
                r.getCliente().getNome(), r.getCliente().getCpf(),
                r.getPacote().getDestino(),
                metodo.toUpperCase(),
                r.getValorTotal());
    }

    private void onCancelar() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma reserva para cancelar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());

        try {
            ReservaRepo rr = new ReservaRepo();
            Optional<Reserva> opt = rr.findById(id);
            if (opt.isPresent()) {
                service.cancelarReserva(opt.get());
                JOptionPane.showMessageDialog(this, "Reserva cancelada com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                loadTable();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao cancelar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onExcluir() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        int confirm = JOptionPane.showConfirmDialog(this, "Deseja apagar o registro desta reserva do sistema?", "Excluir", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                new ReservaRepo().delete(id);
                loadTable();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void loadTable() {
        try {
            tableModel.setRowCount(0);
            List<Reserva> reservas = service.listarTodas();
            DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (Reserva r : reservas) {
                String clienteNome = r.getCliente() != null ? r.getCliente().getNome() : "Desconhecido";
                String pacoteNome = r.getPacote() != null ? r.getPacote().getDestino() : "Indisponível";
                String data = r.getDataReserva() != null ? r.getDataReserva().format(f) : "";

                tableModel.addRow(new Object[]{
                        r.getId(),
                        clienteNome,
                        pacoteNome,
                        data,
                        r.getStatus(),
                        String.format("R$ %.2f", r.getValorTotal())
                });
            }
        } catch (Exception ex) {
            System.err.println("Erro ao carregar tabela: " + ex.getMessage());
        }
    }
}