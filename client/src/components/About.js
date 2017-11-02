import React, { Component } from 'react';
import { Header } from 'semantic-ui-react';

class About extends Component {
  render() {
    return (
      <div>
        <Header as='h1' content='What is Song Wars?' />
        <Header as='h3' content='Song Wars helps you to discover new music!' />
        <p>
          Our goal is to allow you to share your music with the masses, and expose you to rising trends in your area! Have pride when your songs win the week, and open your mind to new music you haven’t seen before.
        </p>
        <p>
          Recommend songs to get them into next week’s bracket, and vote on the songs you think are better. Popular songs can be any song, but Hidden Gems must have a Spotify popularity under 70. At the end of every day from Tuesday to Friday, the votes are counted and winners advance to the next round. On Friday, the best Popular song and the best Hidden Gem of the week go head to head, winner takes all and is memorialized in the Song Wars History Hall of Fame.
        </p>
        <p>
          Song Wars was created by Purdue students: Ben Maxfield, Tom Fanella, Terry Lam, Christian Lock.
        </p>
        <p>
          Enjoy!
        </p>
      </div>
    );
  }
}

export default About;