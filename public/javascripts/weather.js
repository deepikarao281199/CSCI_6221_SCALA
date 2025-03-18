// public/javascripts/weather.js
document.addEventListener('DOMContentLoaded', function() {
  document.getElementById('weatherForm').addEventListener('submit', function(e) {
    e.preventDefault();
    const city = document.getElementById('cityInput').value;
    if (city.trim() === '') return;

    // Fetch current weather
    fetch('/weather/' + encodeURIComponent(city))
      .then(response => response.json())
      .then(data => {
        const resultsDiv = document.getElementById('weatherResults');

        if (data.error) {
          resultsDiv.innerHTML = '<div class="alert alert-danger">' + data.error + '</div>';
          return;
        }

        const weather = data.weather[0];
        resultsDiv.innerHTML =
          '<h3>' + data.city + ', ' + data.country + '</h3>' +
          '<div class="d-flex align-items-center">' +
            '<img src="https://openweathermap.org/img/wn/' + weather.icon + '@2x.png" alt="' + weather.description + '">' +
            '<h2>' + data.temperature.current.toFixed(1) + '°C</h2>' +
          '</div>' +
          '<p class="lead">' + weather.main + ': ' + weather.description + '</p>' +
          '<div class="row mt-3">' +
            '<div class="col-6">' +
              '<p>Feels like: ' + data.temperature.feelsLike.toFixed(1) + '°C</p>' +
              '<p>Min: ' + data.temperature.min.toFixed(1) + '°C</p>' +
              '<p>Max: ' + data.temperature.max.toFixed(1) + '°C</p>' +
            '</div>' +
            '<div class="col-6">' +
              '<p>Humidity: ' + data.humidity + '%</p>' +
              '<p>Pressure: ' + data.pressure + ' hPa</p>' +
              '<p>Wind: ' + data.wind.speed + ' m/s, ' + data.wind.degrees + '°</p>' +
            '</div>' +
          '</div>';

        // Now fetch historical data
        fetchHistoricalData(city);
      })
      .catch(error => {
        console.error('Error:', error);
        document.getElementById('weatherResults').innerHTML =
          '<div class="alert alert-danger">Error fetching weather data</div>';
      });
  });

  function fetchHistoricalData(city) {
    fetch('/weather/' + encodeURIComponent(city) + '/history')
      .then(response => response.json())
      .then(data => {
        const histDiv = document.getElementById('historicalData');

        if (data.length === 0) {
          histDiv.innerHTML = '<p>No historical data available for ' + city + '</p>';
          return;
        }

        let tableHtml =
          '<h4>Historical Weather for ' + city + '</h4>' +
          '<table class="table table-striped">' +
            '<thead>' +
              '<tr>' +
                '<th>Date</th>' +
                '<th>Temperature</th>' +
                '<th>Condition</th>' +
                '<th>Humidity</th>' +
                '<th>Wind</th>' +
              '</tr>' +
            '</thead>' +
            '<tbody>';

        data.forEach(record => {
          const date = new Date(record.timestamp * 1000);
          tableHtml +=
            '<tr>' +
              '<td>' + date.toLocaleDateString() + ' ' + date.toLocaleTimeString() + '</td>' +
              '<td>' + record.temperature.toFixed(1) + '°C</td>' +
              '<td>' + record.main + ': ' + record.description + '</td>' +
              '<td>' + record.humidity + '%</td>' +
              '<td>' + record.windSpeed + ' m/s, ' + record.windDeg + '°</td>' +
            '</tr>';
        });

        tableHtml +=
            '</tbody>' +
          '</table>';

        histDiv.innerHTML = tableHtml;
      })
      .catch(error => {
        console.error('Error:', error);
        document.getElementById('historicalData').innerHTML =
          '<div class="alert alert-danger">Error fetching historical data</div>';
      });
  }
});