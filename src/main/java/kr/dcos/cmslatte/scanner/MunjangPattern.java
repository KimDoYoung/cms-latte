package kr.dcos.cmslatte.scanner;

public enum MunjangPattern {
	 Unknown,
     JustValue,
     JustField,
     //SysField,
     Susik,
     Assign,PlusAssign,MinusAssign,
     For,
     If,ElseIf,Else,
     While,
     Begin,
     End,
     Goto,
     Label,
     QuestionIf,
     Switch,Case,Default,Break,
     Echo,EchoLn,EchoNo,
     Load,Save,
     //SaveVar,LoadVar,DisplayVar,ClearVar,
     //Sleep,
     Clear,Exit,
     Foreach,Continue
}
