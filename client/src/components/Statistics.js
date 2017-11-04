import React, { Component } from 'react';
import { Grid, Header, Loader } from 'semantic-ui-react';

import { getStats } from '../api';

class Statistics extends Component {
  state = { stats: {}, loading: true };

  componentDidMount() {
    getStats()
    .then(data => this.setState({ stats: data, loading: false }))
    .catch(err => this.setState({ loading: false }));
  }

  render() {
    const {
      top_artists,
      votes,
      recommendations,
      top_songs
    } = this.state.stats;
    // console.log(stats);

    return (
      loading ?
        <Loader active={true} size='massive' />
      :
        top_artists && votes && recommendations && top_songs ?
          <Grid columns={3} textAlign='center' divided stackable >

            <Grid.Column>
              <Header as='h2' content='Top Artists' />
              <Grid columns={2} textAlign='center' >
                <Grid.Column>
                  <Header as='h3' content='Artists' />
                  {
                    top_artists.map((artist, i) => (
                      <Header as='h4' key={i} content={artist.name} />
                    ))
                  }
                </Grid.Column>
                <Grid.Column>
                  <Header as='h3' content='Votes' />
                  {
                    top_artists.map((artist, i) => (
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
                  <Header as='h2' content={votes} />
                  <Header as='h2' content={recommendations} />
                </Grid.Column>
              </Grid>
            </Grid.Column>

            <Grid.Column>
              <Header as='h2' content='Top Songs' />
              <Grid columns={2} textAlign='center' >
                <Grid.Column>
                  <Header as='h3' content='Songs' />
                  {
                    top_songs.map((song, i) => (
                      <Header as='h4' key={i} content={song.name} />
                    ))
                  }
                </Grid.Column>
                <Grid.Column>
                  <Header as='h3' content='Votes' />
                  {
                    top_songs.map((song, i) => (
                      <Header as='h4' key={i} content={song.count} />
                    ))
                  }
                </Grid.Column>
              </Grid>
            </Grid.Column>

          </Grid>
        :
          <Header as='h2' color='grey'>No Statistics Availible</Header>
    );
  }
}

export default Statistics;
