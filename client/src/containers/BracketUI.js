import React, { Component } from 'react';
import $ from 'jquery';

window.jQuery = $;

require('../../node_modules/jquery-bracket/dist/jquery.bracket.min.css');
require('../../node_modules/jquery-bracket/dist/jquery.bracket.min.js');

class BracketUI extends Component {

  onMatchHover(...args) {
    console.log(args);
  }

  componentDidMount() {
    var data = {
      teams : [
        ["Piano Man",  "Feels" ],
        ["Batman Theme",  "Scarborough Fair" ],
        ["Believer",  "Look What You Made Me Do" ],
        ["I Don’t Wanna Live Forever",  "Radioactive" ]
      ],
      results : [
        [[1,2, ["Piano Man",  "Feels" ]], [3,4], [5,6], [7,8]],
        [[9,20], [7,2]],
        [[40,3]]
      ]
    };
    
    $('.leftBracket').bracket({
      skipConsolationRound: true,
      teamWidth: 100,
      matchMargin: 50,
      onMatchHover: this.onMatchHover,
      init: data
    });

    $('.rightBracket').bracket({
      skipConsolationRound: true,
      teamWidth: 100,
      matchMargin: 50,
      onMatchHover: this.onMatchHover,
      dir: 'rl',
      init: data
    });
  }

  render() {
    return (
      <div style={{ display: 'flex' }}>
        <span className="leftBracket" />
        <span style={{ minWidth: 150 }} />
        <span className="rightBracket" />
      </div>
    );
  }
}

export default BracketUI;
