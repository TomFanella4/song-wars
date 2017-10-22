import React, { Component } from 'react';
import { Header } from 'semantic-ui-react';

class About extends Component {
  render() {
    return (
      <div>
        <Header as='h1' content='What is Song Wars?' />
        <p>Song Wars allows you to discover new music!</p>
      </div>
    );
  }
}

export default About;