package br.com.alura.loja;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.xstream.XStream;

import br.com.alura.loja.modelo.Carrinho;
import br.com.alura.loja.modelo.Produto;
import br.com.alura.loja.modelo.Projeto;

public class CarrinhoTest {

	private HttpServer server;
	private Client client;
	private WebTarget target;

	@Before
	public void inicializaServidor() {
		server = Servidor.inicializaServidor();
		ClientConfig config = new ClientConfig();
		config.register(new LoggingFilter());
		this.client = ClientBuilder.newClient(config);
		this.target = client.target("http://localhost:8080");

	}

	@After
	public void mataServidor() {
		server.stop();
		System.out.println("Servidor parado");
	}

	@Test
	public void testaQueBuscarUmCarrinhoTrazOCarrinhoEsperado() {
		Carrinho carrinho = target.path("/carrinhos/1").request().get(Carrinho.class);
		Assert.assertTrue(carrinho.getRua().contains("Rua Vergueiro"));

	}

	@Test
	public void testaQueSuportaNovosCarrinhos() {
		Carrinho carrinho = new Carrinho();
		carrinho.adiciona(new Produto(314l, "Tablet", 999, 1));
		carrinho.setRua("Rua Teste de Desenvolvimento");
		carrinho.setCidade("Florianopolis");

		Entity<Carrinho> entity = Entity.entity(carrinho, MediaType.APPLICATION_XML);
		Response response = target.path("/carrinhos").request().post(entity);
		Assert.assertEquals(201, response.getStatus());
		String location = response.getHeaderString("location");
		Carrinho carrinhoCarregado = client.target(location).request().get(Carrinho.class);
		Assert.assertEquals("Tablet", carrinhoCarregado.getProdutos().get(0).getNome());

	}

}
