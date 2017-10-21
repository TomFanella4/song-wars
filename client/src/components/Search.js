import React, { Component } from 'react';
import { Input, Card, Segment } from 'semantic-ui-react';

import { searchSpotify } from '../common/WebServices';
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
          <Card.Group itemsPerRow={2} stackable>
            {this.state.searchResults.map(
              (result, i) => <SearchResult key={result.id} result={result} index={i + 1} />
            )}
          </Card.Group>
        </Segment>
      </div>
    )
  }
}

export default Search;
