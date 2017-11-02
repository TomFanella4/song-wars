import React, { Component } from 'react';
import { Input, Card, Segment, Header } from 'semantic-ui-react';

import { searchSpotify } from '../api';
import SearchResult from '../containers/SearchResult';

class Search extends Component {
  state = { searchResults: [], selectedURI: '', loading: false };
  timmer;

  handleSearchChange(event, data) {
    if (this.timmer) {
      clearTimeout(this.timmer);
    }

    this.timmer = setTimeout(this.search.bind(this, data.value), 250);
  }

  search(searchQuery) {
    if (searchQuery === '') {
      this.setState({
        searchResults: []
      })
      return;
    }

    this.setState({ loading: true });

    searchSpotify(searchQuery)
    .then(results => this.setState({ searchResults: results }))
    .catch(err => console.error(err))
    .then(() => this.setState({ loading: false }))
  }

  shuffle(a) {
    for (let i = a.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [a[i], a[j]] = [a[j], a[i]];
    }
    return a;
  }

  render() {
    const { searchResults, loading } = this.state;
    // console.log(this.state.searchResults);
    
    return (
      <div>
        <Input
          size='massive'
          loading={loading}
          icon='search'
          placeholder='Search...'
          fluid
          onChange={this.handleSearchChange.bind(this)}
        />
        
        <Segment basic>
          {searchResults.length ?
            <Card.Group itemsPerRow={2} stackable>
              {this.shuffle(searchResults.map(
                (result, i) => <SearchResult key={result.id} result={result} index={i + 1} />
              ))}
            </Card.Group>
          :
            <Header as='h2' color='grey'>No Search Results</Header>
          }
        </Segment>
      </div>
    )
  }
}

export default Search;
