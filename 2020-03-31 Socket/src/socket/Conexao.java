package socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Conexao extends Thread {
	private int numero;
	private Observador observador;
	private ObjectOutputStream saida;
	private ObjectInputStream  entrada;
	private AlertaMsg msgAlerta = new AlertaMsg();

	public Conexao(Socket conexao, int numero, Observador observador) {
		try {
			this.numero = numero;
			this.observador = observador;
			saida   = new ObjectOutputStream(conexao.getOutputStream());
			entrada = new ObjectInputStream (conexao.getInputStream ());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		String texto; 
		try {
			do {
				texto = entrada.readUTF();
				System.out.println(texto);

				this.observador.processarMensagem(texto);
				
			} while (! "bye".equals(texto.substring( texto.indexOf(")")+2, texto.length())));
			
		} catch (Exception e) {
			msgAlerta.mensagem("Queda de conexão usuário ("+numero+")");
			e.printStackTrace();
		} finally {
			fecharConexao();
		}
	}

	public void enviarMensagem(String mensagem) {
		try {
			saida.writeUTF(mensagem);
			saida.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void fecharConexao() {
		try {
			entrada.close();
			saida.close();
			this.observador.excluirConexao(this);
			msgAlerta.mensagem("Conexão ("+this.numero+") encerrada.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
