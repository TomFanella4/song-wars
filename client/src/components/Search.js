import React, { Component } from 'react';
import { Input, Card, Segment, Header } from 'semantic-ui-react';

import { searchSpotify } from '../api';
import SearchResult from '../containers/SearchResult';

class Search extends Component {
  state = { searchResults: [], selectedURI: '' };
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

    searchSpotify(searchQuery)
    .then(results => this.setState({ searchResults: results }))
    .catch(err => console.log(err));
  }

  render() {
    const { searchResults } = this.state;
    // console.log(this.state.searchResults);
    
    return (
      <div>
        <Input
          size='massive'
          icon='search'
          placeholder='Search...'
          fluid
          onChange={this.handleSearchChange.bind(this)}
        />
        
        <Segment basic>
          {searchResults.length ?
            <Card.Group itemsPerRow={2} stackable>
              {searchResults.map(
                (result, i) => <SearchResult key={result.id} result={result} index={i + 1} />
              )}
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
