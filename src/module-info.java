module org.ECM.Chess {
    requires java.desktop;
    requires org.junit.jupiter.api;
    requires junit;
    exports Engine.Board;
    exports Engine.Pieces;
    exports Engine.Player;
}
