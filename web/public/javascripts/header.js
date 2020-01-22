$( document ).ready( function() {
  $( '#division_level' ).on( 'change', function() {
      location.href=this.value
  } )

  $( '#leagueUnitNumber' ).on( 'change', function() {
      location.href = this.value
  } )

  $( '#teamLinks' ).on( 'change', function() {
        location.href = this.value
    } )
} )