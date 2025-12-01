package br.edu.uemg.agencia.ui;

import br.edu.uemg.agencia.auth.PermissionUtil;
import br.edu.uemg.agencia.repos.LogRepo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AuditFrame extends JFrame {

    private final LogRepo repo = new LogRepo();
    private DefaultTableModel tableModel;

    public AuditFrame() {
        if (!PermissionUtil.requireAdmin(null, "Acessar Auditoria", "AuditFrame")) {
            dispose();
            return;
        }

        ModernUI.setupTheme(this);
        setTitle("Log de Auditoria & Segurança");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
        loadLogs();
    }

    private void initUI() {
        JPanel main = new JPanel(new BorderLayout(20, 20));
        main.setBackground(ModernUI.COL_BG);
        main.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Auditoria do Sistema 🛡️");
        title.setFont(ModernUI.FONT_BIG);
        title.setForeground(ModernUI.COL_TEXT_H1);

        JButton btnRefresh = ModernUI.createOutlineButton("Atualizar ↻");
        btnRefresh.setPreferredSize(new Dimension(120, 35));
        btnRefresh.addActionListener(e -> loadLogs());

        header.add(title, BorderLayout.WEST);
        header.add(btnRefresh, BorderLayout.EAST);
        main.add(header, BorderLayout.NORTH);

        JPanel tableCard = ModernUI.createCard();
        tableCard.setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new Object[]{"ID", "User", "Ação", "Tela", "Tipo", "Data"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(tableModel);
        ModernUI.styleTable(table);

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(300);

        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);
        main.add(tableCard, BorderLayout.CENTER);

        setContentPane(main);
    }

    private void loadLogs() {
        tableModel.setRowCount(0);
        repo.findAll().forEach(log -> tableModel.addRow(new Object[]{
                log.id, log.usuario, log.acao, log.tela, log.tipo, log.data
        }));
    }
}