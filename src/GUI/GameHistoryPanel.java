package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import Engine.Board.Move;
import Engine.Board.Board;

/**
 * Boiler Plate Game History Panel (deprecated swing/awt).
 *
 * @author Ellie Moore
 * @version 06.12.2020
 */
public class GameHistoryPanel extends JPanel {

    //////////////////////////////////////////////////////////////////////////////
    //<<<<<<<<<<<<<<<<<<<<<<<<<< FIELDS AND CONSTANTS >>>>>>>>>>>>>>>>>>>>>>>>>>//
    //////////////////////////////////////////////////////////////////////////////

    private final DataModel model;
    private final JScrollPane scrollPane;

    private static final Dimension HISTORY_PANEL_DIMENSION;
    static {
        HISTORY_PANEL_DIMENSION = new Dimension(100, 400);
    }

    //////////////////////////////////////////////////////////////////////////////
    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< SET-UP >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>//
    //////////////////////////////////////////////////////////////////////////////

    public GameHistoryPanel(){

        this.setLayout(new BorderLayout());
        this.model = new DataModel();
        final JTable table = new JTable(model);
        table.setRowHeight(15);
        this.scrollPane = new JScrollPane(table);
        scrollPane.setColumnHeaderView(table.getTableHeader());
        scrollPane.setPreferredSize(HISTORY_PANEL_DIMENSION);
        this.add(scrollPane, BorderLayout.CENTER);
        this.setVisible(true);

    }

    //////////////////////////////////////////////////////////////////////////////
    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< PAINTING >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>//
    //////////////////////////////////////////////////////////////////////////////

    public void redo(final Board board, final MoveLog moveLog){
        int currentRow = 0;
        this.model.clear();
        for(final Move move: moveLog.getMoves()) {
            final String moveText = move.toString();
            if (move.getMovedPiece().getPieceAlliance().isWhite()) {
                this.model.setValueAt(moveText, currentRow, 0);
            } else if (move.getMovedPiece().getPieceAlliance().isBlack()) {
                this.model.setValueAt(moveText, currentRow, 1);
                currentRow++;
            }
        }
        if (moveLog.getMoves().size() != 0) {
            final Move lastMove = moveLog.getMoves().get(moveLog.size() - 1);
            final String moveText = lastMove.toString();
            if(lastMove.getMovedPiece().getPieceAlliance().isWhite()){
                this.model.setValueAt(moveText + calculateCheckAndCheckMateHash(board), currentRow, 0);
            }else if(lastMove.getMovedPiece().getPieceAlliance().isBlack()){
                this.model.setValueAt(moveText + calculateCheckAndCheckMateHash(board), currentRow - 1, 1);
            }
        }
        final JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    private String calculateCheckAndCheckMateHash(final Board board) {
        if(board.currentPlayer().isInCheckMate(board.currentPlayer().hasEscapeMoves())) return "#";
        else if(board.currentPlayer().isInCheck()) return "+";
        return "";
    }

    private static class DataModel extends DefaultTableModel {

        private final List<Row> values;
        private static final String[] NAMES = {"White", "Black"};

        DataModel(){
            this.values = new ArrayList<>();
        }

        public void clear(){
            this.values.clear();
            setRowCount(0);
        }

        @Override
        public int getRowCount(){
            if(this.values == null) return 0;
            return this.values.size();
        }

        @Override
        public int getColumnCount(){
            return NAMES.length;
        }

        @Override
        public Object getValueAt(final int row, final int column){
            final Row currentRow = this.values.get(row);
            if(column == 0) return currentRow.getWhiteMove();
            else if(column == 1) return currentRow.getBlackMove();
            return null;
        }

        @Override
        public void setValueAt(final Object value,
                               final int row,
                               final int column){
            final Row currentRow;
            if(this.values.size() <= row){
                currentRow = new Row();
                this.values.add(currentRow);
            }else currentRow = this.values.get(row);
            if(column == 0){
                currentRow.setWhiteMove((String)value);
                fireTableRowsInserted(row, row);
            }else if(column == 1){
                currentRow.setBlackMove((String)value);
                fireTableCellUpdated(row, column);
            }
        }

        @Override
        public Class<?> getColumnClass(final int column){
            return Move.class;
        }

        @Override
        public String getColumnName(final int column){
            return NAMES[column];
        }

    }

    private static class Row {

        private String whiteMove;
        private String blackMove;

        public String getWhiteMove(){
            return this.whiteMove;
        }

        public String getBlackMove(){
            return this.blackMove;
        }

        public void setWhiteMove(final String whiteMove){
            this.whiteMove = whiteMove;
        }

        public void setBlackMove(final String blackMove){
            this.blackMove = blackMove;
        }

    }

}
