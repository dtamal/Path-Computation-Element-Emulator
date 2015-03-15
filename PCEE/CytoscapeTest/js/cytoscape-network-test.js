$(function(){ // on dom ready

var cy = cytoscape({
  container: $('#cy')[0],

  style: cytoscape.stylesheet()
    .selector('node')
      .css({
        'content': 'data(name)',
        'text-valign': 'center',
        'color': 'white',
        'text-outline-width': 2,
        'text-outline-color': '#888'
      })
/*    .selector(':selected')
      .css({
        'background-color': '#61bffc',
        'line-color': '#61bffc',
        'target-arrow-color': '#61bffc',
        'source-arrow-color': '#61bffc',
        'text-outline-color': '#61bffc'
      })*/

    .selector('edge')
          .css({
            'width': 2,
            'line-color': '#888'
          })
/*    .selector(':selected')
          .css({
            'background-color': '#61bffc',
            'line-color': '#61bffc',
            'target-arrow-color': '#61bffc',
            'source-arrow-color': '#61bffc',
            'text-outline-color': '#61bffc'
          })*/
    .selector('.highlighted')
          .css({
            'background-color': '#61bffc',
            'line-color': '#61bffc',
            'target-arrow-color': '#61bffc',
            'text-outline-color': '#61bffc',
            'transition-property': 'background-color, text-outline-color ,line-color, target-arrow-color ',
            'transition-duration': '0.5s'
          }),



  elements: {
    nodes: [
      { data: { id: 'n1', name: 'N1' } },
      { data: { id: 'n2', name: 'N2' } },
      { data: { id: 'n3', name: 'N3' } },
      { data: { id: 'n4', name: 'N4' } },
      { data: { id: 'n5', name: 'N5' } },
      { data: { id: 'n6', name: 'N6' } },
      { data: { id: 'n7', name: 'N7' } },
      { data: { id: 'n8', name: 'N8' } },
      { data: { id: 'n9', name: 'N9' } },
      { data: { id: 'n10', name: 'N10' } },
      { data: { id: 'n11', name: 'N11' } },
      { data: { id: 'n12', name: 'N12' } },
      { data: { id: 'n13', name: 'N13' } },
      { data: { id: 'n14', name: 'N14' } },
      { data: { id: 'n15', name: 'N15' } }
    ],
    edges: [
      { data: { id: 'l1', source: 'n1', target: 'n6' } },
      { data: { id: 'l2', source: 'n1', target: 'n7' } },
      { data: { id: 'l3', source: 'n1', target: 'n8' } },
      { data: { id: 'l4', source: 'n2', target: 'n3' } },
      { data: { id: 'l5', source: 'n2', target: 'n5' } },
      { data: { id: 'l6', source: 'n2', target: 'n6' } },
      { data: { id: 'l7', source: 'n3', target: 'n5' } },
      { data: { id: 'l8', source: 'n3', target: 'n8' } },
      { data: { id: 'l9', source: 'n4', target: 'n5' } },
      { data: { id: 'l10', source: 'n4', target: 'n6' } },
      { data: { id: 'l11', source: 'n6', target: 'n13' } },
      { data: { id: 'l12', source: 'n7', target: 'n10' } },
      { data: { id: 'l13', source: 'n7', target: 'n14' } },
      { data: { id: 'l14', source: 'n8', target: 'n9' } },
      { data: { id: 'l15', source: 'n8', target: 'n15' } },
      { data: { id: 'l16', source: 'n9', target: 'n10' } },
      { data: { id: 'l17', source: 'n9', target: 'n12' } },
      { data: { id: 'l18', source: 'n9', target: 'n15' } },
      { data: { id: 'l19', source: 'n10', target: 'n12' } },
      { data: { id: 'l20', source: 'n11', target: 'n13' } },
      { data: { id: 'l21', source: 'n11', target: 'n14' } },
      { data: { id: 'l22', source: 'n13', target: 'n14' } }
    ]
  },

  layout: {
    name: 'cose',
    padding: 30,
    animate: false,
  }
});

cy.getElementById('n14').addClass('highlighted');
cy.getElementById('l13').addClass('highlighted');
cy.getElementById('n7').addClass('highlighted');
cy.getElementById('l12').addClass('highlighted');
cy.getElementById('n10').addClass('highlighted');


}); // on dom ready