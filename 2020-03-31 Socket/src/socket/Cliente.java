package socket;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;


public class Cliente extends Thread {
	
	private String usuario;
	private boolean estaConectado = false;
	private Socket conexao;
	private ObjectOutputStream saida; 
	private ObjectInputStream  entrada;
	private AlertaMsg msgAlerta = new AlertaMsg();
	
	
	public Cliente(String usuario, String ipServidor, Integer porta) {
		this.usuario = usuario;
		this.estaConectado = configurarCliente(ipServidor, porta);
	}

	private boolean configurarCliente(String ipServidor, Integer porta) {
		try {
			conexao = new Socket(ipServidor, porta);
			msgAlerta.mensagem("Conectado ao servidor");
			saida   = new ObjectOutputStream(conexao.getOutputStream());
			return true;
		} catch (Exception e) {
			msgAlerta.mensagem("FALHA NA CONEXÃO");
			e.printStackTrace();
			return false;
		}
	}
	
	public void run() {
		String texto; 
		try {
			entrada = new ObjectInputStream (conexao.getInputStream ());
			while (true) {
				texto = entrada.readUTF();
				System.out.println("Mensagem recebida: "+texto);
			}
		} catch (Exception e) {
		}
	}

	public boolean getEstaConectado() {
		return this.estaConectado;
	}
	
	public void enviarMensagem(String mensagem) {
		try {
			saida.writeUTF("("+this.usuario+") "+mensagem);
			saida.flush();
		} catch (Exception e) {
			msgAlerta.mensagem("FALHA NA TRANSMISSÃO");
			e.printStackTrace();
			this.estaConectado = false;
		}
	}
	
	public void fecharConexao() {
		try {
			saida.close();
		} catch (Exception e) {
			msgAlerta.mensagem("FALHA NO FECHAMENTO");
			e.printStackTrace();
		}
		try {
			conexao.close();
		} catch (Exception e) {
			msgAlerta.mensagem("FALHA NO ENCERRAMENTO");
			e.printStackTrace();
		}
		msgAlerta.mensagem("Conexão encerrada");
		this.estaConectado = false;
	}

	public static void main(String[] args) {
		String servidor = "localhost";
		Integer porta = 8000;
		String usuario;
		
		Scanner teclado = new Scanner(System.in);
		String texto = "";
		
//		System.out.println("Servidor ? ");
//		servidor = teclado.next();
//		teclado.nextLine();
//
//		System.out.println("Porta ? ");
//		porta = teclado.nextInt();
//		teclado.nextLine();

		System.out.println("Usuário ? ");
		usuario = teclado.nextLine();
		
		Cliente cliente = new Cliente(usuario, servidor, porta);
		cliente.start();
		while (cliente.getEstaConectado()) {
			System.out.println("Digite mensagem (bye = Encerrar chat)? ");
			texto = teclado.nextLine();
            cliente.enviarMensagem(texto);
			if ("bye".equals(texto)) {
				cliente.fecharConexao();
			}
		}

		teclado.close();
		System.out.println("Programa encerrado.");		
	}
}
