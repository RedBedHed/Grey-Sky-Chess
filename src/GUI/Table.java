package GUI;

import Engine.Board.*;
import Engine.Board.Move.MoveFactory;
import Opponent.Minimax;
import Engine.Pieces.King;
import Engine.Player.MoveTransition;
import Engine.Pieces.Piece;
import Engine.Player.Player.PlayerType;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.io.*;
import java.util.concurrent.ExecutionException;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

/**
 * Boiler Plate Chess Table GUI (deprecated swing/awt).
 *
 * @author Ellie Moore
 * @version 06.12.2020
 */
public class Table extends Observable {

    //////////////////////////////////////////////////////////////////////////////
    //<<<<<<<<<<<<<<<<<<<<<<<<<< FIELDS AND CONSTANTS >>>>>>>>>>>>>>>>>>>>>>>>>>//
    //////////////////////////////////////////////////////////////////////////////

    private final JFrame gameFrame;
    private final Setup setup;
    private PlayerType config;
    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private Board gameBoard;
    private final BoardPanel boardPanel;
    private final MoveLog moveLog;
    private Tile sourceTile;
    private Tile destinationTile;
    private Piece userMovedPiece;
    private BoardDirection boardDirection;
    private boolean highLightLegalMoves;
    private Minimax opponent;
    private Move computerMove;
    private final TableGameWatcher watcher;
    private final Runnable computerUpdateAction;
    private OpponentThinkTank thinkTank;

    private static final Color LIGHT_TILE_COLOR;
    private static final Color DARK_TILE_COLOR;
    private static final Dimension OUTER_FRAME_DIMENSION;
    private static final Dimension BOARD_PANEL_DIMENSION;
    private static final Dimension TILE_PANEL_DIMENSION;
    public static final String PIECE_IMAGES_PATH;
    private static final String SAVE_PATH;
    private static final GridLayout BOARD_PANEL_LAYOUT_MANAGER;
    private static final GridBagLayout TILE_PANEL_LAYOUT_MANAGER;
    private static final FileFilter DEFAULT_FILE_FILTER;
    public static final Table INSTANCE;
    private static final Color EXP_COLOR;
    static {
        EXP_COLOR = new Color(0,100,100);
        LIGHT_TILE_COLOR = new Color(173, 216, 230);
        DARK_TILE_COLOR = new Color(65,105,150);
        OUTER_FRAME_DIMENSION = new Dimension(800, 700);
        BOARD_PANEL_DIMENSION = new Dimension(400,400);
        TILE_PANEL_DIMENSION = new Dimension(10,10);
        PIECE_IMAGES_PATH = "C:/Users/evcmo/intelliJWorkspace/Chess/Chess_Pieces/";
        SAVE_PATH = "C:/Users/evcmo/intelliJWorkspace/Chess/save";
        BOARD_PANEL_LAYOUT_MANAGER = new GridLayout(8,8);
        TILE_PANEL_LAYOUT_MANAGER = new GridBagLayout();
        DEFAULT_FILE_FILTER = new FileFilter() {
            @Override
            public boolean accept(File f) {
                if(f.isDirectory()) return true;
                final String name = f.getName();
                return name.endsWith(".pgn");
            }

            @Override
            public String getDescription() {
                return "*.pgn";
            }
        };
        INSTANCE = new Table();
    }

    //////////////////////////////////////////////////////////////////////////////
    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< SET-UP >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>//
    //////////////////////////////////////////////////////////////////////////////

    private Table(){

        this.gameFrame = new JFrame("Grey Sky Chess");
        this.gameFrame.setLayout(new BorderLayout());
        this.setup = new Setup(this.gameFrame, this);
        this.config = setup.getConfig();
        setChessFrameIcon();
        this.gameFrame.setJMenuBar(createTableMenuBar());
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        if(this.config.isComputer())
            this.gameBoard = Board.generateStandardBoard(PlayerType.COMPUTER, PlayerType.COMPUTER);
        else this.gameBoard = Board.generateStandardBoard(PlayerType.USER, PlayerType.COMPUTER);
        this.gameHistoryPanel = new GameHistoryPanel();
        this.takenPiecesPanel = new TakenPiecesPanel();
        this.boardDirection = BoardDirection.NORMAL;
        this.boardPanel = new BoardPanel();
        this.moveLog = new MoveLog();
        this.watcher = new TableGameWatcher();
        this.addObserver(watcher);
        this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
        this.highLightLegalMoves = false;
        this.gameFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.opponent = new Minimax(setup.getSliderValue() << 1, setup.getSliderValue() << 1, 12);
        this.gameFrame.setResizable(false);
        this.gameFrame.setLocationRelativeTo(null);
        this.gameFrame.setVisible(true);
        this.computerUpdateAction = new Runnable(){
            @Override
            public void run() {
                moveMadeUpdate(PlayerType.COMPUTER);
            }
        };
        if(this.config.isComputer()) {
            SwingUtilities.invokeLater(computerUpdateAction);
        }
        System.out.println("White Player: " + gameBoard.whitePlayer().getPlayerType() + " Black Player: " + gameBoard.blackPlayer().getPlayerType());

    }

    public JFrame getFrame(){
        return this.gameFrame;
    }

    private void setChessFrameIcon(){
        try {
            this.gameFrame.setIconImage(ImageIO.read(new File(PIECE_IMAGES_PATH + "BQ.gif")));
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void show() {
        moveLog.clear();
        gameHistoryPanel.redo(gameBoard, moveLog);
        takenPiecesPanel.redo(moveLog);
        boardPanel.drawBoard(gameBoard);
    }

    public void refreshGUI() {
        moveLog.clear();
        takenPiecesPanel.redo(moveLog);
        gameHistoryPanel.redo(gameBoard, moveLog);
        takenPiecesPanel.validate();
        takenPiecesPanel.repaint();
        gameHistoryPanel.validate();
        gameHistoryPanel.repaint();
        boardPanel.drawBoard(gameBoard);
    }

    //////////////////////////////////////////////////////////////////////////////
    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< MENU BAR >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>//
    //////////////////////////////////////////////////////////////////////////////

    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferencesMenu());
        tableMenuBar.add(createSetupMenu());
        return tableMenuBar;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");
        final JMenuItem savePGN = new JMenuItem("Save");
        savePGN.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Open up that pgn file?");
                savePGN(gameBoard);
            }
        });
        fileMenu.add(savePGN);
        final JMenuItem openPGN = new JMenuItem("Load");
        openPGN.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Open up that pgn file?");
                openPGN();
            }
        });
        fileMenu.add(openPGN);
        fileMenu.addSeparator();
        final JMenuItem exitOption = new JMenuItem("Exit");
        exitOption.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                final int option = JOptionPane.showConfirmDialog(
                        gameFrame,
                        "If you exit now, current progress may be lost. " +
                                "Would you like to save?"
                );
                if(option == JOptionPane.YES_OPTION) {
                    savePGN(gameBoard);
                    System.exit(0);
                }
                else if (option == JOptionPane.NO_OPTION){
                    System.exit(0);
                }

            }
        });
        fileMenu.add(exitOption);
        return fileMenu;
    }

    private JMenu createPreferencesMenu(){
        final JMenu preferencesMenu = new JMenu("Preferences");
        final JMenuItem flipBoardPosition = new JMenuItem("Flip Board");
        flipBoardPosition.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                boardDirection = boardDirection.opposite();
                boardPanel.drawBoard(gameBoard);
            }
        });
        preferencesMenu.add(flipBoardPosition);
        preferencesMenu.addSeparator();
        final JCheckBoxMenuItem legalMovesHighLighterCheckBox = new JCheckBoxMenuItem(
                "Hi-Light Legal Moves", false
        );
        legalMovesHighLighterCheckBox.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                highLightLegalMoves = legalMovesHighLighterCheckBox.isSelected();
            }
        });
        preferencesMenu.add(legalMovesHighLighterCheckBox);
        return preferencesMenu;
    }

    private JMenu createSetupMenu(){
        final JMenu setupMenu = new JMenu("Setup");
        final JMenuItem resetOption = new JMenuItem("Reset Board");
        resetOption.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                final int option = JOptionPane.showConfirmDialog(
                        gameFrame,
                        "If you reset now, current progress will be lost. " +
                                "Are you sure?"
                );
                if(option == JOptionPane.YES_OPTION) {
                    if(config.isComputer())
                        gameBoard = Board.generateStandardBoard(PlayerType.COMPUTER, PlayerType.COMPUTER);
                    else gameBoard = Board.generateStandardBoard(PlayerType.USER, PlayerType.COMPUTER);
                    refreshGUI();
                }
            }
        });
        setupMenu.add(resetOption);
        setupMenu.addSeparator();
        final JCheckBoxMenuItem HumanVComputer = new JCheckBoxMenuItem(
                "Human Vs. Computer", config.isUser()
        );
        final JCheckBoxMenuItem ComputerVComputer = new JCheckBoxMenuItem(
                "Computer Vs. Computer", config.isComputer()
        );
        HumanVComputer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final int option = JOptionPane.showConfirmDialog(
                        gameFrame,
                        "If you select this option, current progress will be lost. " +
                                "Are you sure?"
                );
                if(option == JOptionPane.CANCEL_OPTION || option == JOptionPane.NO_OPTION) {
                    HumanVComputer.setState(false);
                    return;
                }
                ComputerVComputer.setState(false);
                HumanVComputer.setState(true);
                config = PlayerType.USER;
                gameBoard = Board.generateStandardBoard(PlayerType.USER, PlayerType.COMPUTER);
                refreshGUI();
                try {
                    thinkTank.cancel(true);
                } catch(Exception ex){
                    System.out.println("Stopped Thinking.");
                }
            }
        });
        setupMenu.add(HumanVComputer);
        ComputerVComputer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!ComputerVComputer.isSelected()){
                    ComputerVComputer.setState(true);
                    return;
                }
                config = PlayerType.COMPUTER;
                gameBoard = Board.generateStandardBoard(PlayerType.COMPUTER, PlayerType.COMPUTER);
                refreshGUI();
                HumanVComputer.setState(false);
                ComputerVComputer.setState(true);
                SwingUtilities.invokeLater(computerUpdateAction);
            }
        });
        setupMenu.add(ComputerVComputer);
        final JMenu changeDifficulty = new JMenu("Change Difficulty");
        JSlider slider = new Setup.DifficultySlider(setup.getSliderValue());
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                opponent = new Minimax(slider.getValue() << 1, slider.getValue() << 1, 12);
            }
        });
        changeDifficulty.add(slider);
        setupMenu.add(changeDifficulty);
        return setupMenu;
    }

    private void openPGN() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(SAVE_PATH));
        fileChooser.setFileFilter(DEFAULT_FILE_FILTER);
        if(fileChooser.showOpenDialog(gameFrame) != JFileChooser.CANCEL_OPTION) {
            try {
                Scanner s = new Scanner(fileChooser.getSelectedFile());
                long start = System.currentTimeMillis();
                gameBoard = Utility.parseFEN(s.nextLine());
                System.out.println("FEN time: " + (System.currentTimeMillis() - start));
                boardPanel.drawBoard(gameBoard);
                moveLog.clear();
                start = System.currentTimeMillis();
                while (s.hasNext()) moveLog.addMove(MoveFactory.parseAndShowcase(s.next()));
                System.out.println("Move time: " + (System.currentTimeMillis() - start));
                gameHistoryPanel.redo(gameBoard, moveLog);
                takenPiecesPanel.redo(moveLog);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void savePGN(Board board){
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(SAVE_PATH));
            fileChooser.setSelectedFile(new File(SAVE_PATH + "/game.pgn"));
            fileChooser.setFileFilter(DEFAULT_FILE_FILTER);
            if(fileChooser.showSaveDialog(gameFrame) != JFileChooser.CANCEL_OPTION) {
                if (fileChooser.getSelectedFile().exists()) {
                    final int option = JOptionPane.showConfirmDialog(
                            gameFrame, "The file you selected already exists. Overwrite?"
                    );
                    if (option == JOptionPane.NO_OPTION) savePGN(board);
                    else if (option == JOptionPane.CANCEL_OPTION) return;
                }
                FileWriter writer = new FileWriter(fileChooser.getSelectedFile());
                writer.write(Utility.boardToFENString(board) + System.getProperty("line.separator"));
                for (Move m : moveLog.getMoves()) writer.write(Utility.moveToPGNString(m));
                writer.close();
            }
        } catch(final IOException e){
            e.printStackTrace();
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< OBSERVER >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>//
    //////////////////////////////////////////////////////////////////////////////

    private static class TableGameWatcher implements Observer {

        @Override
        public void update(final Observable o, final Object arg) {
            final boolean hasEscapeMoves = Table.INSTANCE.gameBoard.currentPlayer().hasEscapeMoves();
            if(arg == Table.INSTANCE.config) {
                if (!Table.INSTANCE.gameBoard.currentPlayer().isInCheckMate(hasEscapeMoves) &&
                        !Table.INSTANCE.gameBoard.currentPlayer().isInStaleMate(hasEscapeMoves) &&
                        !(Table.INSTANCE.gameBoard.currentPlayer().hasInsufficientMaterial() &&
                                Table.INSTANCE.gameBoard.currentPlayer().getOpponent().hasInsufficientMaterial())) {
                    Table.INSTANCE.thinkTank = new OpponentThinkTank();
                    Table.INSTANCE.thinkTank.execute();
                }
            }
            if (Table.INSTANCE.gameBoard.currentPlayer().isInCheckMate(hasEscapeMoves)) {
                JOptionPane.showMessageDialog(
                        Table.INSTANCE.gameFrame,
                        "Game over. " + Table.INSTANCE.gameBoard.currentPlayer() + " is in checkmate."
                );
                System.out.printf("Average time: %.2f%n", Table.INSTANCE.opponent.getAverageExecutionTime());
            }
            if (Table.INSTANCE.gameBoard.currentPlayer().isInStaleMate(hasEscapeMoves)) {
                JOptionPane.showMessageDialog(
                        Table.INSTANCE.gameFrame,
                        "Game over. " + Table.INSTANCE.gameBoard.currentPlayer() + " is in stalemate."
                );
                System.out.printf("Average time: %.2f%n", Table.INSTANCE.opponent.getAverageExecutionTime());
            }
            if (Table.INSTANCE.gameBoard.currentPlayer().hasInsufficientMaterial() &&
                    Table.INSTANCE.gameBoard.currentPlayer().getOpponent().hasInsufficientMaterial()) {
                JOptionPane.showMessageDialog(
                        Table.INSTANCE.gameFrame,
                        "Game over. Draw by insufficient material."
                );
                System.out.printf("Average time: %.2f%n", Table.INSTANCE.opponent.getAverageExecutionTime());
            }
        }

    }

    public void updateGameBoard(final Board board) {
        this.gameBoard = board;
    }

    public void updateComputerMove(final Move move){
        this.computerMove = move;
    }

    private void moveMadeUpdate(final PlayerType type){
        setChanged();
        notifyObservers(type);
    }

    public static boolean isThreeFoldRepetition(final Move move, final MoveLog moveLog) {
        boolean isRepeating = false;
        for(int i = moveLog.size() < 14? 0: moveLog.size() - 14; i < (moveLog.size() > 0? moveLog.size() - 1: 0); i++){
            if(moveLog.getMoves().get(i).equals(move)){
                if(isRepeating){
                    return true;
                }
                isRepeating = true;
            }
        }
        return false;
    }

    private static class OpponentThinkTank extends SwingWorker<Move, String> {

        private OpponentThinkTank() {
        }

        @Override
        protected Move doInBackground() throws Exception {
            return Table.INSTANCE.opponent.execute(Table.INSTANCE.gameBoard, Table.INSTANCE.moveLog, false);
        }

        @Override
        public void done() {
            try {
                final Move bestMove = get();
                Table.INSTANCE.updateComputerMove(bestMove);
                Table.INSTANCE.updateGameBoard(Table.INSTANCE.gameBoard.currentPlayer().makeMove(
                        bestMove, true).getTransitionBoard()
                );
                Table.INSTANCE.boardPanel.drawBoard(Table.INSTANCE.gameBoard);
                Table.INSTANCE.moveLog.addMove(bestMove);
                final MoveLog log = Table.INSTANCE.moveLog;
                Table.INSTANCE.gameHistoryPanel.redo(Table.INSTANCE.gameBoard, log);
                Table.INSTANCE.takenPiecesPanel.redo(log);
                if (isThreeFoldRepetition(bestMove, log) &&
                        isThreeFoldRepetition(log.getMoves().get(log.size() - 1), log)) {
                    JOptionPane.showMessageDialog(
                            Table.INSTANCE.gameFrame,
                            "Game over. Draw by repetition."
                    );
                } else {
                    Table.INSTANCE.moveMadeUpdate(PlayerType.COMPUTER);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    //////////////////////////////////////////////////////////////////////////////
    //<<<<<<<<<<<<<<<<<<<<<<<<<< PAINTING -- UI LOGIC >>>>>>>>>>>>>>>>>>>>>>>>>>//
    //////////////////////////////////////////////////////////////////////////////

    public enum BoardDirection {

        NORMAL {

            @Override
            List<TilePanel> traverse(List<TilePanel> boardTiles) {

                return boardTiles;

            }

            @Override
            BoardDirection opposite() {

                return FLIPPED;

            }

        },
        FLIPPED {

            @Override
            List<TilePanel> traverse(List<TilePanel> boardTiles) {

                return reverseTiles(boardTiles);

            }

            @Override
            BoardDirection opposite() {

                return NORMAL;

            }

        };

        abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);
        abstract BoardDirection opposite();

        public static List<TilePanel> reverseTiles(List<TilePanel> boardTiles){
            List<TilePanel> flippedTiles = new ArrayList<>();
            for(int i = (boardTiles.size() - 1); i >= 0; i--){
                flippedTiles.add(boardTiles.get(i));
            }
            return Collections.unmodifiableList(flippedTiles);
        }

    }

    private class BoardPanel extends JPanel{

        final List<TilePanel> boardTiles;

        BoardPanel(){
            super(BOARD_PANEL_LAYOUT_MANAGER);
            this.boardTiles = new ArrayList<>();
            for(int i = 0; i < Utility.NUMBER_OF_TILES; i++){
                final TilePanel tilePanel = new TilePanel(i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setBackground(EXP_COLOR);
            setBorder(new LineBorder(EXP_COLOR, 10));
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }

        public void drawBoard(final Board board){
            removeAll();
            for(final TilePanel tilePanel: boardDirection.traverse(boardTiles)){
                tilePanel.drawTile(board);
                add(tilePanel);
            }
            validate();
            repaint();
        }

    }

    private class TilePanel extends JPanel{

        private final int tileId;

        TilePanel(final int tileId){
            super(TILE_PANEL_LAYOUT_MANAGER);
            this.tileId = tileId;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            assignOccupiedTileIcon(gameBoard);
            addMouseListener(new MouseListener(){
                @Override
                public void mouseClicked(final MouseEvent e) {
                    if(isRightMouseButton(e)){
                        sourceTile = null;
                        destinationTile = null;
                        userMovedPiece = null;
                    }else if(isLeftMouseButton(e)){
                        //First click. If sourceTile has not been determined...
                        if(sourceTile == null){
                            sourceTile = gameBoard.getTile(tileId);
                            userMovedPiece = sourceTile.getPiece();
                            if(userMovedPiece == null){
                                sourceTile = null;
                            }
                        }else{
                            //Second click. If sourceTile has been determined.
                            destinationTile = gameBoard.getTile(tileId);
                            final Move move = MoveFactory.produce(
                                    gameBoard, sourceTile.getTileCoordinate(), destinationTile.getTileCoordinate()
                            );
                            final MoveTransition transition = gameBoard.currentPlayer().makeMove(move, false);
                            if(transition.getMoveStatus().isDone()){
                                gameBoard = transition.getTransitionBoard();
                                moveLog.addMove(move);
                            }
                            sourceTile = null;
                            destinationTile = null;
                            userMovedPiece = null;
                        }
                        SwingUtilities.invokeLater(new Runnable(){
                            @Override
                            public void run() {
                                gameHistoryPanel.redo(gameBoard, moveLog);
                                takenPiecesPanel.redo(moveLog);
                                boardPanel.drawBoard(gameBoard);
                                if(isThreeFoldRepetition(
                                        (moveLog.getMoves().size() > 0 ?
                                        moveLog.getMoves().get(moveLog.getMoves().size() - 1): null), moveLog) &&
                                        isThreeFoldRepetition(
                                                (moveLog.getMoves().size() > 0 ?
                                                        moveLog.getMoves().get(moveLog.getMoves().size() - 2): null), moveLog)
                                ) {
                                    JOptionPane.showMessageDialog(
                                            Table.INSTANCE.gameFrame,
                                            "Game over. Draw by repetition."
                                    );
                                } else if(gameBoard.currentPlayer().getAlliance().isBlack()){
                                    moveMadeUpdate(PlayerType.USER);
                                }
                            }
                        });
                        validate();
                    }
                }
                @Override
                public void mousePressed(final MouseEvent e) {

                }
                @Override
                public void mouseReleased(final MouseEvent e) {

                }
                @Override
                public void mouseEntered(final MouseEvent e) {

                }
                @Override
                public void mouseExited(final MouseEvent e) {

                }
            });
            validate();

        }

        public void drawTile(final Board board){
            assignTileColor();
            assignOccupiedTileIcon(board);
            highLightLegals(board);
            validate();
            repaint();
        }

        private void assignOccupiedTileIcon(final Board board){
            removeAll();
            if(board.getTile(this.tileId).isTileOccupied()){
                try {
                    final BufferedImage image = ImageIO.read(new File(PIECE_IMAGES_PATH +
                            board.getTile(this.tileId).getPiece().getPieceAlliance().toString().substring(0, 1) +
                            board.getTile(this.tileId).getPiece().toString() + ".gif"));
                    add(new JLabel(new ImageIcon(image)));
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }

        private void highLightLegals(final Board board){
            if(highLightLegalMoves){
                for(final Move move: piecesLegalMoves(board)){
                    if(move.getDestinationCoordinate() == this.tileId) {
                        if (!board.getTile(this.tileId).isTileOccupied()) {
                            setBorder(null);
                            try {
                                add(new JLabel(new ImageIcon(ImageIO.read(new File("C:/Users/evcmo/intelliJWorkspace/Chess/Highlight/green_dot.png")))));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            setBorder(new LineBorder(EXP_COLOR, 5));
                        }
                    }
                }
            }
        }

        private Collection<Move> piecesLegalMoves(final Board board){
            if(userMovedPiece != null && userMovedPiece.getPieceAlliance() == board.currentPlayer().getAlliance()){
                if(userMovedPiece instanceof King)
                    return Utility.concat(board.currentPlayer().getCastles(), userMovedPiece.calculateLegalMoves(board));
                else return userMovedPiece.calculateLegalMoves(board);
            }
            return Collections.emptyList();
        }

        private void assignTileColor() {
            if((sourceTile == null || sourceTile != gameBoard.getTile(this.tileId) &&
                    (destinationTile == null || destinationTile != gameBoard.getTile(this.tileId)))) {
                setBorder(null);
                if (Utility.FIRST_ROW[this.tileId]
                        || Utility.THIRD_ROW[this.tileId]
                        || Utility.FIFTH_ROW[this.tileId]
                        || Utility.SEVENTH_ROW[this.tileId]
                ) {
                    setBackground(this.tileId % 2 != 0 ? LIGHT_TILE_COLOR : DARK_TILE_COLOR);
                } else if (Utility.SECOND_ROW[this.tileId]
                        || Utility.FOURTH_ROW[this.tileId]
                        || Utility.SIXTH_ROW[this.tileId]
                        || Utility.EIGHTH_ROW[this.tileId]) {
                    setBackground(this.tileId % 2 == 0 ? LIGHT_TILE_COLOR : DARK_TILE_COLOR);
                }
            } else {
                if(gameBoard.getTile(this.tileId).getPiece().getPieceAlliance().isWhite())
                    setBorder(new LineBorder(EXP_COLOR, 5));
            }
        }

    }

}
