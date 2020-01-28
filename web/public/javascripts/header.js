$( document ).ready( function() {
  $( '.hrefSelect' ).on( 'change', function() {
      location.href = this.value
  } )
} )