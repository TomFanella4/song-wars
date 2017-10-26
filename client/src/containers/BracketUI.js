import React, { Component } from 'react';
import { Header, Button } from 'semantic-ui-react';
import $ from 'jquery';
import { getCurrentBracket } from '../api';

window.jQuery = $;

require('../../node_modules/jquery-bracket/dist/jquery.bracket.min.css');
require('../../node_modules/jquery-bracket/dist/jquery.bracket.min.js');

class BracketUI extends Component {

  onMatchHover(...args) {
    console.log(args);
  }

  componentDidMount() {
    getCurrentBracket()
    .then(bracket => console.log(bracket))
    .catch(err => console.error(err))

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
        <div>
          <Header as='h1' content='Hidden Gems' icon='diamond' color='red' />
          <span className="leftBracket" />
        </div>
        <span style={{ width: 175, lineHeight: 8 }}>
          <Header content='VS' style={{ fontSize: 75 }} />
          {
            false ?
              <Header as='h1' content={'Winner:\nScarborough Fair'} color='red' />
            :
              <Button content='Vote' color='green' size='huge' onClick={this.props.changeView} />
          }
        </span>
        <div style={{ marginLeft: 40 }}>
          <Header as='h1' content='Popular' icon='star' color='green' />
          <span className="rightBracket" />
        </div>
      </div>
    );
  }
}

export default BracketUI;
