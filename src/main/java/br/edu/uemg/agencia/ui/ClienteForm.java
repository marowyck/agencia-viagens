package br.edu.uemg.agencia.ui;

import br.edu.uemg.agencia.modelo.Cliente;
import br.edu.uemg.agencia.repos.ClienteRepo;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class ClienteForm extends JFrame {

    private final ClienteRepo repo = new ClienteRepo();
    private final Color THEME_COLOR = new Color(59, 130, 246);

    private JTextField tfId;
    private JTextField tfNome;
    private JTextField tfCpf;
    private JTextField tfEmail;
    private JTextField tfTelefone;

    private DefaultTableModel tableModel;
    private JTable table;

    public ClienteForm() {
        setTitle("Gestão de Clientes");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUI();
        loadTableData();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(THEME_COLOR);
        headerPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel titleLabel = new JLabel("Clientes");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.putClientProperty(FlatClientProperties.STYLE, "font: bold +10");

        JLabel subtitleLabel = new JLabel("Gerencie cadastros e informações");
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

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 20, 15));
        formPanel.setBackground(Color.WHITE);

        tfId = createStyledInput("ID (Automático)", false);
        tfNome = createStyledInput("Nome Completo", true);
        tfCpf = createStyledInput("CPF", true);
        tfEmail = createStyledInput("E-mail", true);
        tfTelefone = createStyledInput("Telefone", true);

        formPanel.add(createFieldContainer("Código", tfId));
        formPanel.add(createFieldContainer("Nome do Cliente", tfNome));
        formPanel.add(createFieldContainer("CPF", tfCpf));
        formPanel.add(createFieldContainer("E-mail", tfEmail));
        formPanel.add(createFieldContainer("Telefone", tfTelefone));
        formPanel.add(Box.createGlue());

        contentPanel.add(formPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnNovo = createButton("Novo", Color.WHITE, Color.BLACK, false);
        JButton btnSalvar = createButton("Salvar Dados", THEME_COLOR, Color.WHITE, true);
        JButton btnExcluir = createButton("Excluir", new Color(239, 68, 68), Color.WHITE, true); // Vermelho
        JButton btnLimpar = createButton("Limpar", Color.WHITE, Color.BLACK, false);

        // Ações
        btnNovo.addActionListener(e -> onNovo());
        btnSalvar.addActionListener(e -> onSalvar());
        btnExcluir.addActionListener(e -> onExcluir());
        btnLimpar.addActionListener(e -> clearForm());

        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnNovo);
        buttonPanel.add(btnLimpar);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(btnExcluir);

        JPanel centerContainer = new JPanel(new BorderLayout(0, 15));
        centerContainer.setBackground(Color.WHITE);
        centerContainer.add(buttonPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[] { "ID", "Nome", "CPF", "E-mail", "Telefone" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        centerContainer.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(centerContainer, BorderLayout.CENTER);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onTableSelection();
        });
    }

    private void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(59, 130, 246, 30));
        table.setSelectionForeground(Color.BLACK);

        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(Color.WHITE);
                label.setForeground(new Color(100, 100, 100));
                label.setFont(label.getFont().deriveFont(Font.BOLD));
                label.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, THEME_COLOR)); // Linha colorida embaixo do header
                label.setHorizontalAlignment(JLabel.LEFT);
                return label;
            }
        });
    }

    private JTextField createStyledInput(String placeholder, boolean editable) {
        JTextField tf = new JTextField();
        tf.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        tf.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 5,10,5,10");
        tf.setEditable(editable);
        if(!editable) {
            tf.setBackground(new Color(245, 245, 245));
        }
        return tf;
    }

    private JPanel createFieldContainer(String labelText, JComponent input) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(labelText);
        lbl.putClientProperty(FlatClientProperties.STYLE, "font: bold small");
        lbl.setForeground(new Color(80, 80, 80));
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


    private void onNovo() {
        clearForm();
        tfNome.requestFocus();
    }

    private void onSalvar() {
        String nome = tfNome.getText().trim();
        String cpf = tfCpf.getText().trim();
        String email = tfEmail.getText().trim();
        String telefone = tfTelefone.getText().trim();

        if (nome.isEmpty() || cpf.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome e CPF são obrigatórios.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (tfId.getText().isEmpty()) {
                Cliente c = new Cliente(null, nome, cpf, email, telefone);
                repo.insert(c);
                JOptionPane.showMessageDialog(this, "Cliente inserido com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                Integer id = Integer.valueOf(tfId.getText());
                Cliente c = new Cliente(id, nome, cpf, email, telefone);
                repo.update(c);
                JOptionPane.showMessageDialog(this, "Cliente atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
            loadTableData();
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void onExcluir() {
        if (tfId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja remover este cliente?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            int id = Integer.parseInt(tfId.getText());
            repo.delete(id);
            loadTableData();
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao excluir: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        tfId.setText("");
        tfNome.setText("");
        tfCpf.setText("");
        tfEmail.setText("");
        tfTelefone.setText("");
        table.clearSelection();
    }

    private void onTableSelection() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            tfId.setText(String.valueOf(tableModel.getValueAt(row, 0)));
            tfNome.setText((String) tableModel.getValueAt(row, 1));
            tfCpf.setText((String) tableModel.getValueAt(row, 2));
            tfEmail.setText((String) tableModel.getValueAt(row, 3));
            tfTelefone.setText((String) tableModel.getValueAt(row, 4));
        }
    }

    private void loadTableData() {
        try {
            List<Cliente> clientes = repo.findAll();
            tableModel.setRowCount(0);
            for (Cliente c : clientes) {
                tableModel.addRow(new Object[] {
                        c.getId(),
                        c.getNome(),
                        c.getCpf(),
                        c.getEmail(),
                        c.getTelefone()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}