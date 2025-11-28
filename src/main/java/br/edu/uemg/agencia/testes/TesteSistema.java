package br.edu.uemg.agencia.testes;

import br.edu.uemg.agencia.modelo.PacoteNacional;
import br.edu.uemg.agencia.modelo.PacoteInternacional;
import br.edu.uemg.agencia.servico.ReservaService;
import br.edu.uemg.agencia.repos.DatabaseInitializer;

public class TesteSistema {

    public static void main(String[] args) {
        System.out.println("=== TESTES DE UNIDADE E INTEGRAÇÃO ===");

        try {
            DatabaseInitializer.init();
            System.out.println("[OK] DB Init");
        } catch (Exception e) {
            System.out.println("[FAIL] DB Init");
        }

        PacoteNacional pn = new PacoteNacional(1, "Teste Nac", 5, 1000.0);
        if (Math.abs(pn.calcularValorFinal() - 1050.0) < 0.01) System.out.println("[OK] Cálculo Nacional");
        else System.out.println("[FAIL] Cálculo Nacional: " + pn.calcularValorFinal());

        PacoteInternacional pi = new PacoteInternacional(2, "Teste Int", 5, 1000.0, "USD", 5.0);
        pi.setTaxaEmbarque(200.0);
        if (Math.abs(pi.calcularValorFinal() - 5200.0) < 0.01) System.out.println("[OK] Cálculo Internacional");
        else System.out.println("[FAIL] Cálculo Internacional: " + pi.calcularValorFinal());

        ReservaService service = new ReservaService();
        PacoteNacional pSim = new PacoteNacional(3, "Sim", 1, 1000.0);
        pSim.setImpostoTurismo(0.0);

        double val = service.simularValorFinal(pSim, true);
        if (Math.abs(val - 1025.0) < 0.01) System.out.println("[OK] Simulação Cartão");
        else System.out.println("[FAIL] Simulação Cartão: " + val);

        System.out.println("=== FIM DOS TESTES ===");
    }
}