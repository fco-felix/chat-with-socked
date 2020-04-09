package socket;

public interface Observador {

	public void processarMensagem (String mensagem);
	public void excluirConexao(Conexao conexao);

}
