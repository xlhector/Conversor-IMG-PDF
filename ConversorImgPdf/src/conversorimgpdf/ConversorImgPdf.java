/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conversorimgpdf;

/**
 *
 * @author jitor
 */
import java.io.FileOutputStream;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.Clock;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ConversorImgPdf {
    
    private boolean individualReduction = false;
    private int reduction;
    private Object [] reductionValues = {0,10,20,30,40,50,60,70,80};
    private String currentPath;
    
    private static JFileChooser createFileChooser() {
        JFileChooser result = new JFileChooser();
        FileNameExtensionFilter filtroImagen=new FileNameExtensionFilter("JPG, PNG, JPEG & GIF","jpg","png","gif","jpeg");
         
        result.setMultiSelectionEnabled(true);
        result.setFileFilter(filtroImagen);
        
        return result;
    }

    private static String[] getPaths(JFileChooser fileChooser) {
        int chooserReturn = fileChooser.showDialog(null, "Seleccione las imagenes");
        
        if (chooserReturn == JFileChooser.APPROVE_OPTION){
            File[] files =  fileChooser.getSelectedFiles();
            String [] finalPaths = new String[files.length];
            int position = 0;
            
            if (files.length <= 0 ){
                return new String[0];
            }
            
            for (File file : files){
                finalPaths[position] = file.getAbsolutePath();
                position++;
            }
            
            return finalPaths;
        }
        
        return new String[0];
    }

    /**
     * @param paths Path to file(s)
     */
    
    public ConversorImgPdf(String [] paths) throws FileNotFoundException, DocumentException{
        if (paths.length>1){
            CheckGlobalReduction();
        }
        if(!this.individualReduction){
            this.currentPath = paths[0];
            getReduction();
        }
            
        Document document = new Document();      
        String tempPath = paths[0];
        tempPath = tempPath.replaceAll(".(jpg|png|gif|jpeg)",".pdf");
        
        String output = tempPath;
        FileOutputStream fos = new FileOutputStream(output);
        PdfWriter writer = PdfWriter.getInstance(document, fos);
        writer.open();
        document.open();
        
        for (String path : paths) {
            this.currentPath = path;
            String input     = path; // .gif and .jpg are ok too!
            System.out.println(path);
            try {
                if (this.individualReduction){
                    getReduction();
                }
                
                Image img = Image.getInstance(input);
                img.scalePercent(100-this.reduction);
                document.add(img);
                
            }
            catch (Exception e) {
               e.printStackTrace();
            }
        }
        document.close();
        writer.close();
    }
    /*
    * @param args the command line arguments
    */
    public static void main(String[] args) throws FileNotFoundException, DocumentException {
        if (args.length!=0)
            new ConversorImgPdf(args);
        else{
            JFileChooser fileChooser = createFileChooser();
            String [] paths          = getPaths(fileChooser);
            if (paths.length>0)
                new ConversorImgPdf(paths);
        }
    }

    private void getReduction() {
        String reduction_input = ""+JOptionPane.showInputDialog(null,"Reducción de tamaño(%): " , "TITULO", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(currentPath), reductionValues , "0");
        System.out.println(reduction_input);
        
        if (!isNumeric(reduction_input)){
            reduction_input = "0";
        }
         
        int result = Integer.parseInt(reduction_input);
        if (result>80){
            result = 80;
        }

        this.reduction = result;
    }
    
    public boolean isNumeric(String str) {
        return (str.matches("[+-]?\\d*(\\.\\d+)?") && str.equals("")==false);
    }

    private void CheckGlobalReduction() {
        int result = JOptionPane.showConfirmDialog(null, "Requiere reducción individual de imagenes?");
        
        if (result == 0){
            this.individualReduction = true;
        }
        if (result == 2){
            System.exit(0);
        }
    }
    
}
