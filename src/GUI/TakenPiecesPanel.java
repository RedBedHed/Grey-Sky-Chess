package GUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

import Engine.Board.Move;
import Engine.Pieces.Piece;

/**
 * Boiler Plate Taken Pieces Panel (deprecated swing/awt).
 *
 * @author Ellie Moore
 * @version 06.12.2020
 */
public class TakenPiecesPanel extends JPanel{

    //////////////////////////////////////////////////////////////////////////////
    //<<<<<<<<<<<<<<<<<<<<<<<<<< FIELDS AND CONSTANTS >>>>>>>>>>>>>>>>>>>>>>>>>>//
    //////////////////////////////////////////////////////////////////////////////

    private final JPanel northPanel;
    private final JPanel southPanel;
    private final Comparator<Piece> comp;

    private static final Color PANEL_COLOR;
    private static final EtchedBorder PANEL_BORDER;
    private static final Dimension TAKEN_PIECES_PANEL_DIMENSION;
    private static final int SCALE_FACTOR;
    private static final String PIECES_PATH;
    static {
        PANEL_COLOR = new Color(240, 248, 255);
        PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);
        TAKEN_PIECES_PANEL_DIMENSION = new Dimension(40,80);
        SCALE_FACTOR = 22;
        PIECES_PATH = "C:/Users/evcmo/intelliJWorkspace/Chess/Chess_Pieces/";
    }

    //////////////////////////////////////////////////////////////////////////////
    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< SET-UP >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>//
    //////////////////////////////////////////////////////////////////////////////

    public TakenPiecesPanel(){

        super(new BorderLayout());
        setBackground(PANEL_COLOR);
        setBorder(PANEL_BORDER);
        this.northPanel = new JPanel(new GridLayout(8,2));
        this.southPanel = new JPanel(new GridLayout(8,2));
        this.northPanel.setBackground(PANEL_COLOR);
        this.southPanel.setBackground(PANEL_COLOR);
        this.add(this.northPanel, BorderLayout.NORTH);
        this.add(this.southPanel, BorderLayout.SOUTH);
        setPreferredSize(TAKEN_PIECES_PANEL_DIMENSION);
        comp = new Comparator<Piece>(){
            @Override
            public int compare(Piece o1, Piece o2) {
                return Integer.compare(o1.getPieceValue(), o2.getPieceValue());
            }
        };

    }

    //////////////////////////////////////////////////////////////////////////////
    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< PAINTING >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>//
    //////////////////////////////////////////////////////////////////////////////

    public void redo(final MoveLog moveLog){

        southPanel.removeAll();
        northPanel.removeAll();

        final List<Piece> whiteTakenPieces = new ArrayList<>();
        final List<Piece> blackTakenPieces = new ArrayList<>();

        for(final Move move: moveLog.getMoves()){
            if(move.isAttack()){
                final Piece takenPiece = move.getAttackedPiece();
                if(takenPiece.getPieceAlliance().isWhite()){
                    whiteTakenPieces.add(takenPiece);
                }else if(takenPiece.getPieceAlliance().isBlack()){
                    blackTakenPieces.add(takenPiece);
                }else{
                    throw new RuntimeException("Pieces MUST have an alliance.");
                }
            }
        }

        Collections.sort(whiteTakenPieces, comp);
        Collections.sort(blackTakenPieces, comp);

        for(final Piece takenPiece: whiteTakenPieces){

            try{
                final BufferedImage image = ImageIO.read(new File(PIECES_PATH
                        + takenPiece.getPieceAlliance().toString().substring(0, 1)
                        + takenPiece.toString() + ".gif"));
                final ImageIcon icon = new ImageIcon(image);
                final int width = icon.getIconWidth() - SCALE_FACTOR;
                this.northPanel.add(new JLabel(new ImageIcon(icon.getImage().getScaledInstance(width, width, Image.SCALE_SMOOTH))));
            }catch(final IOException e){
                e.printStackTrace();
            }

        }

        for(final Piece takenPiece: blackTakenPieces){

            try{
                final BufferedImage image = ImageIO.read(new File(PIECES_PATH
                        + takenPiece.getPieceAlliance().toString().substring(0, 1)
                        + takenPiece.toString() + ".gif"));
                final ImageIcon icon = new ImageIcon(image);
                final int width = icon.getIconWidth() - SCALE_FACTOR;
                this.southPanel.add(new JLabel(new ImageIcon(icon.getImage().getScaledInstance(width, width, Image.SCALE_SMOOTH))));
            }catch(final IOException e){
                e.printStackTrace();
            }

        }
        validate();
        repaint();
    }

}
