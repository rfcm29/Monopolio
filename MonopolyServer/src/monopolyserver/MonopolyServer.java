/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monopolyserver;

import java.io.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rfcm2
 */
public class MonopolyServer {
    
    static Vector<ClientHandler> ar = new Vector<>();
    static int i = 0;

    public static void main(String[] args) throws IOException {
        System.out.println("Servidor aceita conexões.");
        ServerSocket ss = new ServerSocket(1234);

        Socket s;
        while(true){
            s = ss.accept();
            System.out.println("Novo client recebido : " + s);

            ObjectInputStream dis = new ObjectInputStream(s.getInputStream());
            ObjectOutputStream dos = new ObjectOutputStream(s.getOutputStream());

        ClientHandler mtch = new ClientHandler(s, "client " + i, dis, dos);
        Thread t = new Thread(mtch);

        System.out.println("Adiciona cliente "+ i + " à lista ativa.");
        ar.add(mtch);
        t.start();

        i++;
        }
    }
    
    private static class ClientHandler implements Runnable {
        private String name;
        final ObjectInputStream dis;
        final ObjectOutputStream dos;
        Socket s;
        boolean isloggedin;
        private ClientHandler(Socket s, String string, 
        ObjectInputStream dis, ObjectOutputStream dos) {
            this.s = s;
            this.dis = dis;
            this.dos = dos;
            this.name = string;
            this.isloggedin = true;
        }
    
        @Override
        public void run() {
            String recebido;

            while(true){
            try {
                recebido = dis.readUTF();
                System.out.println(recebido);
                if(recebido.endsWith("logout")){
                    this.isloggedin = false;
                    this.s.close();
                break;
                }
    
                StringTokenizer st = new StringTokenizer(recebido, "#");
                String MsgToSend = st.nextToken();
                String recipient = null;
                try {recipient = st.nextToken();} catch(Exception esc){};
                if(recipient != null){
                    for(ClientHandler mc: MonopolyServer.ar){
                        if(mc.name.equals(recipient) && mc.isloggedin) {
                            mc.dos.writeUTF(name + " : " + MsgToSend); 
                        break;
                        }
                    }
                }
                else {
                    for(ClientHandler mc: MonopolyServer.ar){
                        if(mc.name.equals(name) && mc.isloggedin){
                            mc.dos.writeUTF(name + ":" + MsgToSend);
                        }
                            }
                            }
                } catch (IOException ex) {
                    Logger.getLogger(MonopolyServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
}
