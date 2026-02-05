/**
 * Copyright (C) 2012 Jason Gedge <http://www.gedge.ca>
 *
 * This file is part of the OpGraph project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
grammar MathExpression;

@header {
package ca.phon.opgraph.nodes.math.parser;
}

prog: expr EOF
    | EOF
    ;

expr: '-' expr                            # Negate
    | expr op=('*' | '/' | '%') expr      # MulDivMod
    | expr op=('+' | '-') expr            # AddSub
    | '(' expr ')'                         # Parens
    | REAL                                 # Real
    | INT                                  # Int
    | ID                                   # Variable
    ;

WS   : [ \t]+ -> skip ;
ID   : LETTER (LETTER | DIGIT | '_')* ;
REAL : DIGIT+ '.' DIGIT+ ;
INT  : DIGIT+ ;

fragment LETTER : [a-zA-Z] ;
fragment DIGIT  : [0-9] ;
