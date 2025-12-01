package br.edu.uemg.agencia.ui;

import br.edu.uemg.agencia.modelo.Cliente;
import br.edu.uemg.agencia.repos.ClienteRepo;
import br.edu.uemg.agencia.util.ExternalService;
import br.edu.uemg.agencia.util.FormatUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ClienteForm extends JFrame {

    private final ClienteRepo repo = new ClienteRepo();
    private JTextField tfId, tfNome, tfEmail, tfLogradouro, tfBairro, tfCidade, tfUf;
    private JFormattedTextField tfCpf, tfTelefone, tfCep;
    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel lblPontos;

    public ClienteForm() {
        ModernUI.setupTheme(this);
        setTitle("Gestão de Clientes");
        setSize(1100, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
        loadData();
    }

    private void initUI() {
        JPanel main = new JPanel(new BorderLayout(20,20));
        main.setBackground(ModernUI.COL_BG);
        main.setBorder(new EmptyBorder(20,20,20,20));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Gerenciar Clientes");
        title.setFont(ModernUI.FONT_H1);
        title.setForeground(ModernUI.COL_TEXT_H1);

        lblPontos = new JLabel("Pontos: 0");
        lblPontos.setFont(ModernUI.FONT_BOLD);
        lblPontos.setForeground(ModernUI.BRAND);

        header.add(title, BorderLayout.WEST);
        header.add(lblPontos, BorderLayout.EAST);
        main.add(header, BorderLayout.NORTH);

        JPanel form = ModernUI.createCard();
        form.setLayout(new BorderLayout(0,15));

        JPanel grid = new JPanel(new GridLayout(3, 4, 15, 15));
        grid.setOpaque(false);

        tfId = ModernUI.createInput("Auto"); tfId.setEditable(false);
        tfNome = ModernUI.createInput("Nome");
        tfCpf = FormatUtil.createFormattedField("###.###.###-##");
        tfEmail = ModernUI.createInput("Email");
        tfTelefone = FormatUtil.createFormattedField("(##) #####-####");

        tfCep = FormatUtil.createFormattedField("#####-###");
        JPanel cepP = new JPanel(new BorderLayout(5,0)); cepP.setOpaque(false);
        cepP.add(tfCep, BorderLayout.CENTER);
        JButton btnCep = ModernUI.createOutlineButton("🔍"); btnCep.setPreferredSize(new Dimension(40,30));
        btnCep.addActionListener(e -> buscarCep());
        cepP.add(btnCep, BorderLayout.EAST);

        tfLogradouro = ModernUI.createInput("Rua");
        tfBairro = ModernUI.createInput("Bairro");
        tfCidade = ModernUI.createInput("Cidade");
        tfUf = ModernUI.createInput("UF");

        grid.add(ModernUI.createLabelGroup("ID", tfId));
        grid.add(ModernUI.createLabelGroup("Nome", tfNome));
        grid.add(ModernUI.createLabelGroup("CPF", tfCpf));
        grid.add(ModernUI.createLabelGroup("Email", tfEmail));
        grid.add(ModernUI.createLabelGroup("Telefone", tfTelefone));
        grid.add(ModernUI.createLabelGroup("CEP", cepP));
        grid.add(ModernUI.createLabelGroup("Rua", tfLogradouro));
        grid.add(ModernUI.createLabelGroup("Bairro", tfBairro));
        grid.add(ModernUI.createLabelGroup("Cidade", tfCidade));
        grid.add(ModernUI.createLabelGroup("UF", tfUf));
        grid.add(new JLabel("")); grid.add(new JLabel(""));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT)); btns.setOpaque(false);

        JButton btnFav = ModernUI.createOutlineButton("⭐ Ver Favoritos");
        JButton btnHist = ModernUI.createOutlineButton("📜 Histórico");
        JButton btnClr = ModernUI.createOutlineButton("Limpar");
        JButton btnDel = ModernUI.createButton("Excluir"); btnDel.setBackground(ModernUI.DANGER);
        JButton btnSav = ModernUI.createButton("Salvar");

        btnClr.addActionListener(e -> clear());
        btnDel.addActionListener(e -> delete());
        btnSav.addActionListener(e -> save());
        btnHist.addActionListener(e -> showHistory());
        btnFav.addActionListener(e -> showFavoritos());

        btns.add(btnFav);
        btns.add(btnHist);
        btns.add(btnClr);
        btns.add(btnDel);
        btns.add(btnSav);

        form.add(grid, BorderLayout.CENTER);
        form.add(btns, BorderLayout.SOUTH);

        JPanel list = ModernUI.createCard(); list.setLayout(new BorderLayout());
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nome", "CPF", "Cidade", "Pontos"},0);
        table = new JTable(tableModel);
        ModernUI.styleTable(table);
        list.add(new JScrollPane(table));
        table.getSelectionModel().addListSelectionListener(e -> { if(!e.getValueIsAdjusting()) pop(); });

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, form, list);
        split.setOpaque(false); split.setBorder(null); split.setDividerLocation(350);
        main.add(split, BorderLayout.CENTER);
        setContentPane(main);
    }

    private void showFavoritos() {
        if(tfId.getText().equals("Auto")) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente primeiro.");
            return;
        }
        int id = Integer.parseInt(tfId.getText());
        Cliente c = new Cliente();
        c.setId(id);
        c.setNome(tfNome.getText());

        new FavoritosFrame(c).setVisible(true);
    }

    private void showHistory() {
        if(tfId.getText().equals("Auto")) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente primeiro.");
            return;
        }
        int id = Integer.parseInt(tfId.getText());
        var lista = repo.findHistorico(id);

        StringBuilder sb = new StringBuilder();
        sb.append("HISTÓRICO DE COMPRAS:\n\n");
        if(lista.isEmpty()) sb.append("Nenhuma reserva encontrada.");
        else {
            for(var r : lista) {
                sb.append(String.format("• [#%d] Data: %s | Pacote: %s | Total: R$ %.2f (%s)\n",
                        r.getId(), r.getDataReserva().toLocalDate(),
                        r.getPacote().getDestino(), r.getValorTotal(), r.getStatus()));
            }
        }

        JTextArea area = new JTextArea(sb.toString());
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setRows(15); area.setColumns(50); area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Histórico do Cliente", JOptionPane.INFORMATION_MESSAGE);
    }

    private void buscarCep() {
        new Thread(() -> {
            String[] r = ExternalService.getEnderecoViaCep(tfCep.getText());
            if(r!=null) SwingUtilities.invokeLater(() -> {
                tfLogradouro.setText(r[0]); tfBairro.setText(r[1]); tfCidade.setText(r[2]); tfUf.setText(r[3]);
            });
        }).start();
    }

    private void save() {
        try {
            Cliente c = new Cliente();
            String id = tfId.getText();
            if(!id.equals("Auto") && !id.isEmpty()) c.setId(Integer.parseInt(id));
            c.setNome(tfNome.getText()); c.setCpf(tfCpf.getText()); c.setEmail(tfEmail.getText());
            c.setTelefone(tfTelefone.getText()); c.setCep(tfCep.getText());
            c.setLogradouro(tfLogradouro.getText()); c.setBairro(tfBairro.getText());
            c.setCidade(tfCidade.getText()); c.setUf(tfUf.getText());

            if(c.getId()==null) repo.insert(c);
            else {
                repo.findById(c.getId()).ifPresent(o -> c.setPontosFidelidade(o.getPontosFidelidade()));
                repo.update(c);
            }
            loadData(); clear(); JOptionPane.showMessageDialog(this, "Salvo!");
        } catch(Exception e) { JOptionPane.showMessageDialog(this, "Erro: "+e.getMessage()); }
    }

    private void delete() {
        if(!tfId.getText().equals("Auto")) { repo.delete(Integer.parseInt(tfId.getText())); loadData(); clear(); }
    }

    private void pop() {
        int r = table.getSelectedRow();
        if(r>=0) repo.findById((int)tableModel.getValueAt(r,0)).ifPresent(c -> {
            tfId.setText(String.valueOf(c.getId())); tfNome.setText(c.getNome());
            tfCpf.setText(c.getCpf()); tfEmail.setText(c.getEmail()); tfTelefone.setText(c.getTelefone());
            tfCep.setText(c.getCep()); tfLogradouro.setText(c.getLogradouro());
            tfBairro.setText(c.getBairro()); tfCidade.setText(c.getCidade()); tfUf.setText(c.getUf());
            lblPontos.setText("Pontos: " + c.getPontosFidelidade());
        });
    }

    private void loadData() {
        tableModel.setRowCount(0);
        repo.findAll().forEach(c -> tableModel.addRow(new Object[]{c.getId(), c.getNome(), c.getCpf(), c.getCidade(), c.getPontosFidelidade()}));
    }

    private void clear() {
        tfId.setText("Auto"); tfNome.setText("Nome"); tfCpf.setValue(null); tfCpf.setText("");
        tfEmail.setText("Email"); tfTelefone.setValue(null); tfTelefone.setText("");
        tfCep.setText(""); tfLogradouro.setText(""); tfBairro.setText(""); tfCidade.setText(""); tfUf.setText("");
        lblPontos.setText("Pontos: 0"); table.clearSelection();
    }
}