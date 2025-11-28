package br.edu.uemg.agencia.ui;

import br.edu.uemg.agencia.modelo.Cliente;
import br.edu.uemg.agencia.repos.ClienteRepo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;

public class ClienteForm extends JFrame {

    private final ClienteRepo repo = new ClienteRepo();
    private JTextField tfId, tfNome, tfEmail;
    private JFormattedTextField tfCpf, tfTelefone;
    private DefaultTableModel tableModel;
    private JTable table;
    private JButton btnSave;

    public ClienteForm() {
        ModernUI.applyTheme(this);
        setTitle("Clientes | Agência++");
        setSize(1100, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initUI();
        loadData();
    }

    private void initUI() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(ModernUI.COL_BG);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ModernUI.COL_BORDER));

        JLabel title = new JLabel("Gerenciar Clientes");
        title.setFont(ModernUI.FONT_H1);
        title.setBorder(new EmptyBorder(20, 30, 20, 30));
        header.add(title, BorderLayout.WEST);
        main.add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 20));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel formCard = ModernUI.createCard();
        formCard.setLayout(new BorderLayout(0, 20));

        JPanel grid = new JPanel(new GridLayout(2, 3, 20, 20));
        grid.setOpaque(false);

        tfId = ModernUI.createInput("Automático");
        tfId.setEditable(false);
        tfId.setBackground(new Color(245, 245, 245));

        tfNome = ModernUI.createInput("Nome Completo");
        tfCpf = createMaskedField("###.###.###-##");
        tfEmail = ModernUI.createInput("email@exemplo.com");
        tfTelefone = createMaskedField("(##) #####-####");

        grid.add(ModernUI.createFieldGroup("ID", tfId));
        grid.add(ModernUI.createFieldGroup("Nome Completo", tfNome));
        grid.add(ModernUI.createFieldGroup("CPF", tfCpf));
        grid.add(ModernUI.createFieldGroup("E-mail", tfEmail));
        grid.add(ModernUI.createFieldGroup("Telefone", tfTelefone));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);

        JButton btnClear = ModernUI.createButton("Novo / Limpar", false);
        JButton btnDel = ModernUI.createButton("Excluir", false);
        btnDel.setForeground(ModernUI.COL_ACCENT_DANGER);
        btnSave = ModernUI.createButton("Salvar", true);

        btnSave.addActionListener(e -> onSave());
        btnClear.addActionListener(e -> clear());
        btnDel.addActionListener(e -> onDelete());

        actions.add(btnClear);
        actions.add(btnDel);
        actions.add(btnSave);

        formCard.add(grid, BorderLayout.CENTER);
        formCard.add(actions, BorderLayout.SOUTH);

        JPanel tableCard = ModernUI.createCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(0, 0, 0, 0));

        tableModel = new DefaultTableModel(new Object[]{"ID", "Nome", "CPF", "Email"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        ModernUI.styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        tableCard.add(scroll, BorderLayout.CENTER);

        content.add(formCard, BorderLayout.NORTH);
        content.add(tableCard, BorderLayout.CENTER);

        main.add(content, BorderLayout.CENTER);
        setContentPane(main);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                populate();
            }
        });
    }

    private JFormattedTextField createMaskedField(String mask) {
        try {
            MaskFormatter mf = new MaskFormatter(mask);
            mf.setPlaceholderCharacter('_');
            mf.setValueContainsLiteralCharacters(false);
            JFormattedTextField ftf = new JFormattedTextField(mf);
            ftf.setFont(ModernUI.FONT_BODY);
            ftf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ModernUI.COL_BORDER),
                    new EmptyBorder(8, 12, 8, 12)
            ));
            return ftf;
        } catch (Exception e) {
            return new JFormattedTextField();
        }
    }

    private void loadData() {
        tableModel.setRowCount(0);
        repo.findAll().forEach(c -> tableModel.addRow(new Object[]{c.getId(), c.getNome(), c.getCpf(), c.getEmail()}));
    }

    private void populate() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;

        try {
            Object idObj = table.getValueAt(selectedRow, 0);
            int id = Integer.parseInt(idObj.toString());

            repo.findById(id).ifPresent(c -> {
                tfId.setText(String.valueOf(c.getId()));
                tfNome.setText(c.getNome());

                tfCpf.setValue(null);
                tfCpf.setText(c.getCpf());

                tfEmail.setText(c.getEmail());

                tfTelefone.setValue(null);
                tfTelefone.setText(c.getTelefone());

                btnSave.setText("Atualizar");
            });

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar cliente: " + e.getMessage());
        }
    }

    private void onSave() {
        try {
            Integer id = null;
            if (!tfId.getText().trim().isEmpty() && !tfId.getText().equals("Automático")) {
                id = Integer.parseInt(tfId.getText().trim());
            }

            Cliente c = new Cliente(
                    id,
                    tfNome.getText(),
                    tfCpf.getText(),
                    tfEmail.getText(),
                    tfTelefone.getText()
            );

            if (c.getId() == null) {
                repo.insert(c);
                JOptionPane.showMessageDialog(this, "Cliente cadastrado!");
            } else {
                repo.update(c);
                JOptionPane.showMessageDialog(this, "Cliente atualizado!");
            }

            loadData();
            clear();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage());
        }
    }

    private void onDelete() {
        if (!tfId.getText().isEmpty() && !tfId.getText().equals("Automático")) {
            int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir?", "Excluir", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                repo.delete(Integer.parseInt(tfId.getText()));
                loadData();
                clear();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela para excluir.");
        }
    }

    private void clear() {
        tfId.setText("Automático");
        tfNome.setText("");
        tfCpf.setValue(null); tfCpf.setText("");
        tfEmail.setText("");
        tfTelefone.setValue(null); tfTelefone.setText("");

        table.clearSelection();
        btnSave.setText("Salvar");
    }
}