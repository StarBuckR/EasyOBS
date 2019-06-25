
package controller;

import java.awt.BorderLayout;
import java.io.IOException;
import javax.swing.JPanel;
import javax.swing.JTable;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class DersProgrami {
    WebController wc;
    Connection.Response loginForm;
    
    public DersProgrami(WebController wc, Connection.Response loginForm){
        this.wc = wc;
        this.loginForm = loginForm;
    }
    public JPanel makePanel() throws IOException {
        JPanel jp = new JPanel();
        
        Document doc = wc.connectToLink("https://obs.sdu.edu.tr/Birimler/Ogrenci/DersProgrami.aspx", loginForm);
        Element e = doc.getElementById("ctl00_ContentPlaceHolder1_gridGorevlendirme");
        
        String[] columnNames = {
            "Kod",
            "Ders-Saati",
            "Şube",
            "Görevlendirilen",
            "Gün",
            "Derslik",
            "Başlangıç",
            "Bitiş",
            "Sınıf"
        };
        Object data[][] = new Object[e.child(0).childNodeSize() - 3][10];
        
        for(int i = 1; i < e.child(0).childNodeSize() - 2; i++){
            data[i - 1][0] = e.child(0).child(i).child(0).text();
            for(int j = 0; j < 9; j++){
                if(j >= 6){
                    data[i-1][j] = e.child(0).child(i).child(j + 1).text();
                }
                else{
                    data[i - 1][j] = e.child(0).child(i).child(j).text();
                }
            }
        }
        
        JTable table = new JTable(data, columnNames);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(45);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(15);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(40);
        table.getColumnModel().getColumn(5).setPreferredWidth(200);
        table.getColumnModel().getColumn(6).setPreferredWidth(30);
        table.getColumnModel().getColumn(7).setPreferredWidth(30);
        table.getColumnModel().getColumn(8).setPreferredWidth(15);
        table.setRowHeight(30);
        
        jp.setLayout(new BorderLayout());
        jp.add(table.getTableHeader(), BorderLayout.PAGE_START);
        jp.add(table, BorderLayout.CENTER);
        
        return jp;
    }
}
