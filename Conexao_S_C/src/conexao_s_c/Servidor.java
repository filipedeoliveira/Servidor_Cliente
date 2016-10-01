/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conexao_s_c;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Filipe Oliveira
 */
public class Servidor {

    public static void main(String[] args) throws IOException {
        //inicia servidor
        new Servidor(12345).executa();
    }

    private int porta;
    private List<PrintStream> clientes;

    public Servidor(int porta) {
        this.porta = porta;
        this.clientes = new ArrayList<PrintStream>();
    }

    public void executa() throws IOException {
        ServerSocket servidor = new ServerSocket(this.porta);
        System.out.println("Porta 12345 aberta!");

        while (true) {
            //aceita cliente
            Socket cliente = servidor.accept();
            System.out.println("Nova Conexão com o cliente " + cliente.getInetAddress().getHostAddress());

            //adicionar saida do cliente À lista
            PrintStream ps = new PrintStream(cliente.getOutputStream());
            this.clientes.add(ps);

            //tratar de clinete com nova thread
            TrataCliente tc = new TrataCliente(cliente.getInputStream(), this);
            new Thread(tc).start();
        }
    }

    public void distribuiMensagem(String msg) {
        //enviar mensagen para todos
        for (PrintStream cliente : this.clientes) {
            cliente.println(msg);
        }
    }

}

class TrataCliente implements Runnable {

    private InputStream cliente;
    private Servidor servidor;

    public TrataCliente(InputStream cliente, Servidor servidor) {
        this.cliente = cliente;
        this.servidor = servidor;
    }

    public void run() {
        //quando chegada de msg, distribui para todos
        Scanner s = new Scanner(this.cliente);
        while (s.hasNextLine()) {
            servidor.distribuiMensagem(s.nextLine());
        }
        s.close();
    }
}
