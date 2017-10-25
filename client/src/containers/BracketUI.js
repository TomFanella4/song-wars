import React, { Component } from 'react';
import $ from 'jquery'

window.jQuery = $;

require('../../node_modules/jquery-bracket/dist/jquery.bracket.min.css');
require('../../node_modules/jquery-bracket/dist/jquery.bracket.min.js');

class BracketUI extends Component {

  componentDidMount() {
    var data = {
      teams : [
        ["Song 1",  "Song 2" ],
        ["Song 3",  "Song 4" ],
        ["Song 5",  "Song 6" ],
        ["Song 7",  "Song 8" ]
      ],
      results : [
        [[1,2], [3,4], [5,6], [7,8]],
        [[9,20], [7,2]],
        [[40,3]]
      ]
    };
    
    $('.leftBracket').bracket({
      skipConsolationRound: true,
      init: data
    });

    $('.rightBracket').bracket({
      skipConsolationRound: true,
      dir: 'rl',
      init: data
    });
  }

  render() {
    return (
      <div style={{ display: 'flex' }}>
        <span className="leftBracket" />
        <span style={{ width: 150 }} />
        <span className="rightBracket" />
      </div>
    );
  }
}

export default BracketUI;
