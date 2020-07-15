export function buildTooltipContent(micAtDistance: any, acAtDistance: any): string {
  let test = `
  <table class="table">
  <thead>
  <tr>
  <th>MIC</th>
  <th>Distance</th>
  </tr>
  </thead>
  <tbody>
  `;
  for (const distance in micAtDistance) {
    if (micAtDistance.hasOwnProperty(distance)) {
      test += `
      <tr>
      <td>${micAtDistance[distance]}</td>
      <td>${distance}</td>
      </tr>
      `;
    }
  }

  test += '</tbody></table><br>';
  test += `
  <table class="table">
  <thead>
  <tr>
  <th>AC</th>
  <th>Distance</th>
  </tr>
  </thead>
  <tbody>
  `;

  for (const distance in acAtDistance) {
    if (acAtDistance.hasOwnProperty(distance)) {
      test += `
      <tr>
      <td>${acAtDistance[distance]}</td>
      <td>${distance}</td>
      </tr>
      `;
    }
  }

  test += '</tbody></table>';

  return test;
}
