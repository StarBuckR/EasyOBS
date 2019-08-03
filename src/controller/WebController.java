
package controller;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebController{
    String username;
    String password;
    Connection.Response loginForm;
    
    public WebController(String username, String password) throws IOException{
        this.username = username;
        this.password = password;
        
        this.loginForm = Jsoup.connect("https://obs.sdu.edu.tr/index.aspx")
                .method(Connection.Method.GET)
                .execute();
        
        connectObs();
    }
    
    public void connectObs() throws IOException{
        
        Document doc = loginForm.parse();
        
        Elements hiddenElems = doc.select("input[type=hidden]");
        HashMap<String, String> nameValue = new HashMap<>();

        for(Element elem : hiddenElems) {
            nameValue.put(elem.attr("name"), elem.attr("value"));
        }
        
        Connection.Response document = Jsoup.connect("https://obs.sdu.edu.tr/index.aspx")
                .data("textKulID", username)
                .data("textSifre", password)
                .data("buttonTamam", "Giriş")
                .data("HiddenOgrId", "")
                .data(nameValue)
                .cookies(loginForm.cookies())
                .method(Connection.Method.POST)
                .execute();
        
        Document doc2 = connectToLink("https://obs.sdu.edu.tr/Birimler/Ogrenci/Bilgilerim.aspx", loginForm);
        
        Element e2 = doc2.getElementById("ctl00_ContentPlaceHolder1_gridOgrenciKnt");
        String gno = e2.child(0).child(1).child(8).text();
        
        doc2 = connectToLink("https://obs.sdu.edu.tr/Birimler/Ogrenci/DonemDersleri.aspx", loginForm);
        
        Element e = doc2.getElementById("ctl00_ContentPlaceHolder1_DersGrid");
        createFrame(e, gno);
    }
    
    public Document connectToLink(String link, Connection.Response loginForm) throws IOException{
        Document doc = Jsoup.connect(link)
                .cookies(loginForm.cookies())
                .get();
        return doc;
    }
    
    public void createFrame(Element e, String gno){
        String[] columnNames = {
            "Ders Adı",
            "Vize",
            "Final",
            "Büt Öncesi Ort",
            "Büt Öncesi Harf",
            "Büt",
            "Ort",
            "Harf"
        };
        Object data[][] = new Object[e.child(0).childNodeSize() - 1][8];
        
        JFrame jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension dimension = new Dimension(1000, 600);
        jFrame.setMinimumSize(dimension);
        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel jp = new JPanel();
        tabbedPane.addTab("Ders Bilgileri", jp);
        
        DersProgrami dp = new DersProgrami(this, loginForm);
        try {
            tabbedPane.addTab("Ders Programı", dp.makePanel());
        } catch (IOException ex) {
            Logger.getLogger(WebController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        for(int i = 1; i < e.child(0).childNodeSize() - 1; i++){
            data[i-1][0] = e.child(0).child(i).child(1).text();
            for(int j = 1; j < 8; j++){
                data[i-1][j] = e.child(0).child(i).child(j + 2).text();
            }
        }
        data[e.child(0).childNodeSize() - 2][0] = "GNO: " + gno;
        
        JTable table = new JTable(data, columnNames);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.setRowHeight(30);
        
        jp.setLayout(new BorderLayout());
        jp.add(table.getTableHeader(), BorderLayout.PAGE_START);
        jp.add(table, BorderLayout.CENTER);
        jFrame.getContentPane().add(tabbedPane);
        
        jFrame.setVisible(true);
    }
}
