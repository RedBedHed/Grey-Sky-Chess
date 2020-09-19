package GUI;

import Engine.Pieces.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

// Boilerplate
public class PromotionChooser extends JDialog {

    private Piece promotionPiece;
    private static final String PIECE_IMAGES_PATH = "C:/Users/evcmo/intelliJWorkspace/Chess/Chess_Pieces/";

    public PromotionChooser(final JFrame parent, final Pawn piece){
        super(parent, "Promotion Piece", true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(new Dimension(350,100));
        setLayout(new BorderLayout());
        add(new ButtonPanel(this, piece), BorderLayout.CENTER);
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    public Piece getPiece(){
        return promotionPiece;
    }

    private final class ButtonPanel extends JPanel {

        public ButtonPanel(final JDialog parent, final Pawn piece) {
            setLayout(new GridBagLayout());
            try {
                JButton QueenButton = new JButton(new ImageIcon(ImageIO.read(
                        new File(PIECE_IMAGES_PATH + piece.getPieceAlliance().toString().charAt(0) + "Q.gif")))
                );
                JButton RookButton = new JButton(new ImageIcon(ImageIO.read(new File(PIECE_IMAGES_PATH +
                        piece.getPieceAlliance().toString().charAt(0) + "R.gif")))
                );
                JButton BishopButton = new JButton(new ImageIcon(ImageIO.read(new File(PIECE_IMAGES_PATH +
                        piece.getPieceAlliance().toString().charAt(0) + "B.gif")))
                );
                JButton KnightButton = new JButton(new ImageIcon(ImageIO.read(new File(PIECE_IMAGES_PATH +
                        piece.getPieceAlliance().toString().charAt(0) + "N.gif")))
                );
                QueenButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        promotionPiece = Queen.defaultInstance(piece.getPiecePosition(), piece.getPieceAlliance());
                        parent.dispose();
                    }
                });
                RookButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        promotionPiece = Rook.defaultInstance(piece.getPiecePosition(), piece.getPieceAlliance());
                        parent.dispose();
                    }
                });
                BishopButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        promotionPiece = Bishop.defaultInstance(piece.getPiecePosition(), piece.getPieceAlliance());
                        parent.dispose();
                    }
                });
                KnightButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        promotionPiece = Knight.defaultInstance(piece.getPiecePosition(), piece.getPieceAlliance());
                        parent.dispose();
                    }
                });
                add(QueenButton);
                add(RookButton);
                add(BishopButton);
                add(KnightButton);
            } catch(IOException e){
                e.printStackTrace();
            }
        }

    }

}
