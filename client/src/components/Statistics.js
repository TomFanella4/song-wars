import React, { Component } from 'react';
import { Grid, Header, Loader } from 'semantic-ui-react';

import { getStats } from '../api';

class Statistics extends Component {
  state = { stats: null };

  componentDidMount() {
    getStats()
    .then(data => this.setState({ stats: data }))
    .catch(err => console.error(err));
  }

  render() {
    const { stats } = this.state;
    console.log(stats);

    return (
        stats ?
          <Grid columns={3} textAlign='center' divided stackable >

            <Grid.Column>
              <Header as='h2' content='Top Artists' />
              <Grid columns={2} textAlign='center' >
                <Grid.Column>
                  <Header as='h3' content='Artists' />
                  {
                    stats.top_artists.map((artist, i) => (
                      <Header as='h4' key={i} content={artist.name} />
                    ))
                  }
                </Grid.Column>
                <Grid.Column>
                  <Header as='h3' content='Votes' />
                  {
                    stats.top_artists.map((artist, i) => (
                      <Header as='h4' key={i} content={artist.count} />
                    ))
                  }
                </Grid.Column>
              </Grid>
            </Grid.Column>

            <Grid.Column>
              <Header as='h2' content='Totals' textAlign='center' />
              <Grid columns={2} textAlign='center' >
                <Grid.Column>
                  <Header as='h2' content='Votes' />
                  <Header as='h2' content='Recommendations' />
                </Grid.Column>
                <Grid.Column>
                  <Header as='h2' content={stats.votes} />
                  <Header as='h2' content={stats.recommendations} />
                </Grid.Column>
              </Grid>
            </Grid.Column>

            <Grid.Column>
              <Header as='h2' content='Top Songs' />
              <Grid columns={2} textAlign='center' >
                <Grid.Column>
                  <Header as='h3' content='Songs' />
                  {
                    stats.top_songs.map((song, i) => (
                      <Header as='h4' key={i} content={song.name} />
                    ))
                  }
                </Grid.Column>
                <Grid.Column>
                  <Header as='h3' content='Votes' />
                  {
                    stats.top_songs.map((song, i) => (
                      <Header as='h4' key={i} content={song.count} />
                    ))
                  }
                </Grid.Column>
              </Grid>
            </Grid.Column>

          </Grid>
        :
          <Loader active={true} size='massive' />
    );
  }
}

export default Statistics;
