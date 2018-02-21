package model;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.FileOutputStream;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

public class Matrix extends JPanel {

        /**
         * construct the PDF current situation  
         * @param parkingLot 
		 * 
		 */
		public void makeMatrix(ParkingLot parkingLot){

		   ParkingSlot[][][] _parkingSlot = parkingLot.get_lot();
		   JFrame frame = new JFrame("Testing");
           frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
           frame.setLayout(new BorderLayout());
           frame.setSize(1000, 1000);
           frame.setBackground(Color.WHITE);
           JPanel fullContainer = new JPanel();
           fullContainer.setBounds(0, 0, 800, 800);
           fullContainer.setLayout(new BoxLayout(fullContainer, BoxLayout.Y_AXIS));
           fullContainer.setBackground(Color.WHITE);
           //fullContainer.setSize(800,800);
           Matrix blueMat = new Matrix(1, 1, SpotStatus.Busy);
           Matrix greenMat = new Matrix(1, 1, SpotStatus.Available);
           Matrix yellowMat = new Matrix(1, 1, SpotStatus.Reserved);
           Matrix redMat = new Matrix(1, 1, SpotStatus.Unavailable);
           
           JLabel blueLabel = new JLabel("Occupied");
           blueLabel.setSize(50, 50);
           blueLabel.setAlignmentX(LEFT_ALIGNMENT);
           blueLabel.setBackground(Color.WHITE);
           
           JLabel greenLabel = new JLabel("Available");
           greenLabel.setSize(50, 50);
           greenLabel.setAlignmentX(LEFT_ALIGNMENT);
           greenLabel.setBackground(Color.WHITE);
           
           JLabel yellowLabel = new JLabel("Reserved");
           yellowLabel.setSize(50, 50);
           yellowLabel.setAlignmentX(LEFT_ALIGNMENT);
           yellowLabel.setBackground(Color.WHITE);
           
           JLabel redLabel = new JLabel("Unavailable");
           redLabel.setSize(50, 50);
           redLabel.setAlignmentX(LEFT_ALIGNMENT);
           redLabel.setBackground(Color.WHITE);
           
           blueMat.setSize(20, 20);
           blueMat.setAlignmentX(LEFT_ALIGNMENT);
           blueMat.setBackground(Color.WHITE);
           greenMat.setSize(20, 20);
           greenMat.setAlignmentX(LEFT_ALIGNMENT);
           greenMat.setBackground(Color.WHITE);
           yellowMat.setSize(20, 20);
           yellowMat.setAlignmentX(LEFT_ALIGNMENT);
           yellowMat.setBackground(Color.WHITE);
           redMat.setSize(20, 20);
           redMat.setAlignmentX(LEFT_ALIGNMENT);
           redMat.setBackground(Color.WHITE);
           fullContainer.add(blueLabel);
           fullContainer.add(blueMat);
           fullContainer.add(greenLabel);
           fullContainer.add(greenMat);
           fullContainer.add(yellowLabel);
           fullContainer.add(yellowMat);
           fullContainer.add(redLabel);
           fullContainer.add(redMat);
           for (int i=0;i<parkingLot.getDepth();i++){
	           JLabel label = new JLabel("floor " + (i+1) +":");
	           //JPanel textPanel = new JPanel();
	           label.setSize(100,100);
	           label.setAlignmentX(LEFT_ALIGNMENT);
	           JPanel mat = new Matrix(parkingLot.getHeight(), parkingLot.getWidth(),i, _parkingSlot);
	           mat.setBackground(Color.WHITE);
	           mat.setSize(200, 200);
	           mat.setAlignmentX(LEFT_ALIGNMENT);
	           fullContainer.add(label);
	           fullContainer.add(mat);
           }
           frame.add(fullContainer);
           frame.pack();
           frame.setLocationRelativeTo(null);
           frame.setVisible(false);
           
           Document document = new Document();
           try {
               PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("./test.pdf"));
               document.open();
               PdfContentByte contentByte = writer.getDirectContent();
               PdfTemplate template = contentByte.createTemplate(500, 500);
               Graphics g = template.createGraphicsShapes(500, 500);
               

//               panel.add(label);
//               panel.setSize(50,50);
//               panel.setBackground(Color.BLUE);
//               panel.print(g2);
//               panel.print(g2);
               fullContainer.print(g);
               g.dispose();
               contentByte.addTemplate(template, 30, 300);

               
           } catch (Exception e) {
               e.printStackTrace();
           }
           finally{
               if(document.isOpen()){
                   document.close();
               }
           }
           
    }
		private static final long serialVersionUID = 1L;
	
		/**
		 * construct matrix from cellPane objects
		 * @param height
		 * @param width
		 * @param depth
		 * @param parkingSlot
		 */
		public Matrix(int height, int width, int depth, ParkingSlot[][][] parkingSlot) {
            setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    gbc.gridx = col;
                    gbc.gridy = row;

                    CellPane cellPane = new CellPane(row, col, depth, parkingSlot);
                    Border border = null;
                    if (row < height - 1) {
                        if (col < width - 1) {
                            border = new MatteBorder(1, 1, 0, 0, Color.GRAY);
                        } else {
                            border = new MatteBorder(1, 1, 0, 1, Color.GRAY);
                        }
                    } else {
                        if (col < width - 1) {
                            border = new MatteBorder(1, 1, 1, 0, Color.GRAY);
                        } else {
                            border = new MatteBorder(1, 1, 1, 1, Color.GRAY);
                        }
                    }
                    cellPane.setBorder(border);
                    add(cellPane, gbc);
                }
            }
        }
		

		public Matrix(int height, int width, SpotStatus e) {
            setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    gbc.gridx = col;
                    gbc.gridy = row;

                    CellPane cellPane = new CellPane(row, col, e);
                    Border border = null;
                    if (row < height - 1) {
                        if (col < width - 1) {
                            border = new MatteBorder(1, 1, 0, 0, Color.GRAY);
                        } else {
                            border = new MatteBorder(1, 1, 0, 1, Color.GRAY);
                        }
                    } else {
                        if (col < width - 1) {
                            border = new MatteBorder(1, 1, 1, 0, Color.GRAY);
                        } else {
                            border = new MatteBorder(1, 1, 1, 1, Color.GRAY);
                        }
                    }
                    cellPane.setBorder(border);
                    add(cellPane, gbc);
                }
            }
        }
    }

