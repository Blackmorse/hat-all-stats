$( document ).ready( function() {
  $( '#divisionLevel' ).on( 'change', function() {
      location.href=this.value
  } )

  $( '#leagueUnitNumber' ).on( 'change', function() {
      location.href = this.value
  } )

  $( '#teamLinks' ).on( 'change', function() {
        location.href = this.value
  } )

  $( '#seasonNumber' ).on( 'change', function() {
      location.href = this.value
  } )
} )