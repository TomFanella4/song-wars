import React from 'react';
import { Button } from 'semantic-ui-react';
import '../styles/Bracket.css';

const bracketColumns = [
  [['Song 1', 'Song 2'], ['Song 3', 'Song 4'], ['Song 5', 'Song 6'], ['Song 7', 'Song 8']],
  [['Song 1', 'Song 4'], ['Song 5', 'Song 8']],
  [['Song 4', 'Song 5']],
  [['Song 4']],
  [['Song B', 'Song G']],
  [['Song B', 'Song D'], ['Song E', 'Song G']],
  [['Song A', 'Song B'], ['Song C', 'Song D'], ['Song E', 'Song F'], ['Song G', 'Song H']],
]

// const getSide = i => {
//   if (i < bracketColumns.length / 2)
//     return 'right';
//   return 'left'
// }

const Bracket = () => (

  // <main id="tournament">
  //   {bracketColumns.map((bracketRows, i) => (

  //     <ul className={`round round-${i}`}>

  //       {bracketRows.map(bracketMatch => (
  //         <div>
  //           <li className="spacer">&nbsp;</li>

  //           <li className="game game-top winner">{bracketMatch[0]} <Button icon='play' size='mini' /></li>
  //           {bracketMatch.length > 1 ? 
  //             <div>
  //               <li className={`game game-spacer-${getSide(i)}`}>&nbsp;</li>
  //               <li className="game game-bottom ">{bracketMatch[1]} <Button icon='play' size='mini' /></li>
  //             </div>
  //             :
  //             null
  //           }

  //         </div>
  //       ))}
        
  //       <li className="spacer">&nbsp;</li>

  //     </ul>
  //   ))}
  // </main>

  <main id="tournament">
    <ul className="round round-1">
      <li className="spacer">&nbsp;</li>
      
      <li className="game game-top winner">{bracketColumns[0][0][0]} <Button icon='play' size='mini' /></li>
      <li className="game game-spacer-right">&nbsp;</li>
      <li className="game game-bottom ">{bracketColumns[0][0][1]} <Button icon='play' size='mini' /></li>
  
      <li className="spacer">&nbsp;</li>
      
      <li className="game game-top winner">{bracketColumns[0][1][0]} <Button icon='play' size='mini' /></li>
      <li className="game game-spacer-right">&nbsp;</li>
      <li className="game game-bottom ">{bracketColumns[0][1][1]} <Button icon='play' size='mini' /></li>
  
      <li className="spacer">&nbsp;</li>
      
      <li className="game game-top ">{bracketColumns[0][2][0]} <Button icon='play' size='mini' /></li>
      <li className="game game-spacer-right">&nbsp;</li>
      <li className="game game-bottom winner">{bracketColumns[0][2][1]} <Button icon='play' size='mini' /></li>
  
      <li className="spacer">&nbsp;</li>
      
      <li className="game game-top ">{bracketColumns[0][3][0]} <Button icon='play' size='mini' /></li>
      <li className="game game-spacer-right">&nbsp;</li>
      <li className="game game-bottom winner">{bracketColumns[0][3][1]} <Button icon='play' size='mini' /></li>
  
      <li className="spacer">&nbsp;</li>
    </ul>
    <ul className="round round-2">
      <li className="spacer">&nbsp;</li>
      
      <li className="game game-top winner">{bracketColumns[1][0][0]} <Button icon='play' size='mini' /></li>
      <li className="game game-spacer-right">&nbsp;</li>
      <li className="game game-bottom ">{bracketColumns[1][0][1]} <Button icon='play' size='mini' /></li>
  
      <li className="spacer">&nbsp;</li>
      
      <li className="game game-top ">{bracketColumns[1][1][0]} <Button icon='play' size='mini' /></li>
      <li className="game game-spacer-right">&nbsp;</li>
      <li className="game game-bottom winner">{bracketColumns[1][1][1]} <Button icon='play' size='mini' /></li>
  
      <li className="spacer">&nbsp;</li>
    </ul>
    <ul className="round round-3">
      <li className="spacer">&nbsp;</li>
      
      <li className="game game-top winner">{bracketColumns[2][0][0]} <Button icon='play' size='mini' /></li>
      <li className="game game-spacer-right">&nbsp;</li>
      <li className="game game-bottom ">{bracketColumns[2][0][1]} <Button icon='play' size='mini' /></li>
      
      <li className="spacer">&nbsp;</li>
    </ul>
    <ul className="round round-4">
      <li className="spacer">&nbsp;</li>
      
      <li className="game game-bottom winner">{bracketColumns[3][0][0]} <Button icon='play' size='mini' /></li>
      
      <li className="spacer">&nbsp;</li>
    </ul>
    <ul className="round round-5">
      <li className="spacer">&nbsp;</li>
      
      <li className="game game-top winner">{bracketColumns[4][0][0]} <Button icon='play' size='mini' /></li>
      <li className="game game-spacer-left">&nbsp;</li>
      <li className="game game-bottom ">{bracketColumns[4][0][1]} <Button icon='play' size='mini' /></li>
      
      <li className="spacer">&nbsp;</li>
    </ul>
    <ul className="round round-6">
      <li className="spacer">&nbsp;</li>
      
      <li className="game game-top winner">{bracketColumns[5][0][0]} <Button icon='play' size='mini' /></li>
      <li className="game game-spacer-left">&nbsp;</li>
      <li className="game game-bottom ">{bracketColumns[5][0][1]} <Button icon='play' size='mini' /></li>
  
      <li className="spacer">&nbsp;</li>
      
      <li className="game game-top ">{bracketColumns[5][1][0]} <Button icon='play' size='mini' /></li>
      <li className="game game-spacer-left">&nbsp;</li>
      <li className="game game-bottom winner">{bracketColumns[5][1][1]} <Button icon='play' size='mini' /></li>
  
      <li className="spacer">&nbsp;</li>
    </ul>
    <ul className="round round-7">
      <li className="spacer">&nbsp;</li>
      
      <li className="game game-top winner">{bracketColumns[6][0][0]} <Button icon='play' size='mini' /></li>
      <li className="game game-spacer-left">&nbsp;</li>
      <li className="game game-bottom ">{bracketColumns[6][0][1]} <Button icon='play' size='mini' /></li>
  
      <li className="spacer">&nbsp;</li>
      
      <li className="game game-top winner">{bracketColumns[6][1][0]} <Button icon='play' size='mini' /></li>
      <li className="game game-spacer-left">&nbsp;</li>
      <li className="game game-bottom ">{bracketColumns[6][1][1]} <Button icon='play' size='mini' /></li>
  
      <li className="spacer">&nbsp;</li>
      
      <li className="game game-top ">{bracketColumns[6][2][0]} <Button icon='play' size='mini' /></li>
      <li className="game game-spacer-left">&nbsp;</li>
      <li className="game game-bottom winner">{bracketColumns[6][2][1]} <Button icon='play' size='mini' /></li>
  
      <li className="spacer">&nbsp;</li>
      
      <li className="game game-top ">{bracketColumns[6][3][0]} <Button icon='play' size='mini' /></li>
      <li className="game game-spacer-left">&nbsp;</li>
      <li className="game game-bottom winner">{bracketColumns[6][3][1]} <Button icon='play' size='mini' /></li>
  
      <li className="spacer">&nbsp;</li>
    </ul>
  </main>
)

export default Bracket;