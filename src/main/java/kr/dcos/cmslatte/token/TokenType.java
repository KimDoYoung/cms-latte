package kr.dcos.cmslatte.token;

public enum TokenType {
	Unknown,
    Boolean,
    Integer,
    String,
    Double,
    Date,
    Function,
    ArrayInit,
    TableInit,
    StringArray,
    Field,
    ArrayField,MatrixField,


    Assign,         // =
    Plus, Minus, // + , -
    Multiple,Divide, // *,/
    LogicalEqual,          // ==
    GreatThanOrEqual, LessThanOrEqual, // >= , <=
    LogicalNotEqual,  GreatThan, LessThan, // != > <
    Mod,                  // %
    LeftParen, RightParen, // ( )
    LeftBracket,RightBracket, //[ ]
    LeftBrace,RightBrace, // { }
    Comma,
    Not, //!
    LogicalAnd, // and
    LogicalOr,  // or
    Period,

    For,To,Step,Down,
    If,Elseif,Else,
    While,
    Begin,End,Label,Goto,
    Colon,Question, // a==b ? c=1: c=2;
    Switch,Case,Default,Break,
    Echo,EchoLn,EchoNo,
    Load, From,//Of,
    Save,With,Override,File,
    //SaveVar,LoadVar,
    //Sleep,
    Clear,
    Variables,Output,Functions, //주의 function이 있음
    
    Exit,
    Foreach,In, Continue,

    //PlusAssign, MinusAssign, // += , -=
    PlusPlus, MinusMinus    // ++, --
	
}
