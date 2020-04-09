package socket;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fco-felix
 *
 */
public class Servidor implements Observador {
	private ServerSocket servidor;
	private Socket conexao;
	private List<Conexao> listaConexoes = new ArrayList<>();
	private AlertaMsg msgAlerta = new AlertaMsg();

	public Servidor (Integer porta) {
		configurarServidor(porta);
		processarConexao();
	}
	
	private void configurarServidor(Integer porta) {
		try {
			servidor = new ServerSocket(porta);
			msgAlerta.mensagem("Servidor INICIADO");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void processarConexao() {
		int contador = 0;
		try {
			do {
				System.out.println("- Aguardando nova conexão");
				conexao = servidor.accept(); 

				System.out.println("- Nova conexão recebida => "+ (++contador));
				Conexao cliente = new Conexao(conexao, contador, this );
				listaConexoes.add(cliente);

				cliente.start();

			} while (true); // TODO a definir: fim do servidor ?
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void processarMensagem(String mensagem) {
		for (Conexao destinatario : listaConexoes) {
			destinatario.enviarMensagem( mensagem);
		}
	}

	@Override
	public void excluirConexao(Conexao conexao) {
		listaConexoes.remove(conexao);
	}

//	private void fecharServidor() {
//		// TODO definir na interface gráfica ação para botão
//		try {
//			servidor.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		msgAlerta.mensagem("Servidor FINALIZADO");
//	}

	public static void main(String[] args) {
		Integer porta = 8000;
		
//		Scanner teclado = new Scanner(System.in);
//		System.out.println("Porta ? ");
//		porta = teclado.nextInt();

		Servidor server = new Servidor(porta);
	}

}
