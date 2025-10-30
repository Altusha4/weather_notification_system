const API_BASE = 'http://localhost:8080/api/weather';

// Weather data configuration
const cityWeatherData = {
    "Astana": {
        baseTemp: 15,
        tempRange: { min: -20, max: 35 },
        baseHumidity: 60,
        humidityRange: { min: 30, max: 90 },
        basePressure: 1013,
        pressureRange: { min: 980, max: 1040 },
        baseWind: 3,
        windRange: { min: 0, max: 15 },
        description: "Continental climate with extreme temperatures"
    },
    "Almaty": {
        baseTemp: 18,
        tempRange: { min: -10, max: 38 },
        baseHumidity: 55,
        humidityRange: { min: 25, max: 85 },
        basePressure: 950,
        pressureRange: { min: 930, max: 970 },
        baseWind: 2,
        windRange: { min: 0, max: 12 },
        description: "Mountain climate with mild winters"
    },
    "Karaganda": {
        baseTemp: 12,
        tempRange: { min: -25, max: 32 },
        baseHumidity: 50,
        humidityRange: { min: 20, max: 80 },
        basePressure: 1005,
        pressureRange: { min: 980, max: 1020 },
        baseWind: 4,
        windRange: { min: 1, max: 18 },
        description: "Industrial city with variable climate"
    },
    "Pavlodar": {
        baseTemp: 14,
        tempRange: { min: -22, max: 34 },
        baseHumidity: 65,
        humidityRange: { min: 35, max: 95 },
        basePressure: 1008,
        pressureRange: { min: 985, max: 1030 },
        baseWind: 5,
        windRange: { min: 2, max: 20 },
        description: "Northern city with humid climate"
    },
    "Shymkent": {
        baseTemp: 20,
        tempRange: { min: -5, max: 42 },
        baseHumidity: 45,
        humidityRange: { min: 15, max: 75 },
        basePressure: 995,
        pressureRange: { min: 970, max: 1010 },
        baseWind: 3,
        windRange: { min: 0, max: 14 },
        description: "Southern city with hot summers"
    },
    "Aktobe": {
        baseTemp: 13,
        tempRange: { min: -18, max: 36 },
        baseHumidity: 58,
        humidityRange: { min: 28, max: 88 },
        basePressure: 1002,
        pressureRange: { min: 975, max: 1025 },
        baseWind: 6,
        windRange: { min: 2, max: 22 },
        description: "Western city with windy conditions"
    }
};

const weatherDescriptions = {
    hot: ["Sunny and hot", "Clear skies", "Heat wave", "Dry and sunny"],
    warm: ["Partly cloudy", "Pleasant weather", "Mild conditions", "Clear"],
    mild: ["Cloudy", "Overcast", "Light breeze", "Comfortable"],
    cool: ["Chilly", "Cool breeze", "Mostly cloudy", "Fresh"],
    cold: ["Cold", "Freezing", "Frosty", "Bitter cold"],
    extreme: ["Extreme conditions", "Severe weather", "Storm warning", "Dangerous cold"]
};

// App state
let appState = {
    updateCount: 0,
    observerCount: 0,
    currentStrategy: 'None',
    stationName: 'Weather Station'
};

let temperatureChart = null;
let temperatureData = {
    labels: [],
    temperatures: [],
    cities: [],
    timestamps: []
};

// Utility functions
function showNotification(message, type = 'info', duration = 4000) {
    const container = document.getElementById('notificationContainer');
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    const icons = {
        success: '✅',
        error: '❌',
        info: 'ℹ️'
    };
    notification.innerHTML = `
        <span class="notification-icon">${icons[type]}</span>
        <span class="notification-message">${message}</span>
    `;
    container.appendChild(notification);
    setTimeout(() => {
        notification.style.animation = 'slideIn 0.3s ease reverse';
        setTimeout(() => notification.remove(), 300);
    }, duration);
}

function updateAppState(newState) {
    appState = { ...appState, ...newState };
    updateUI();
}

function updateUI() {
    document.getElementById('stationName').textContent = appState.stationName;
    document.getElementById('currentStrategy').textContent = appState.currentStrategy;
    document.getElementById('currentStrategyDisplay').textContent = appState.currentStrategy;

    document.getElementById('observerCount').textContent = appState.observerCount;
    document.getElementById('updateCount').textContent = appState.updateCount;
}

// Weather data generation functions
function generateCityWeather(cityName) {
    const cityData = cityWeatherData[cityName];
    if (!cityData) {
        return generateRandomWeather(cityName);
    }

    const tempVariation = (Math.random() - 0.5) * 10;
    const temperature = cityData.baseTemp + tempVariation;

    const humidityVariation = (Math.random() - 0.5) * 20;
    const humidity = Math.max(cityData.humidityRange.min,
        Math.min(cityData.humidityRange.max,
            cityData.baseHumidity + humidityVariation));

    const pressureVariation = (Math.random() - 0.5) * 20;
    const pressure = Math.max(cityData.pressureRange.min,
        Math.min(cityData.pressureRange.max,
            cityData.basePressure + pressureVariation));

    const windVariation = Math.random() * 4;
    const windSpeed = Math.max(cityData.windRange.min,
        Math.min(cityData.windRange.max,
            cityData.baseWind + windVariation));

    let descriptionType;
    if (temperature >= 30) descriptionType = 'hot';
    else if (temperature >= 20) descriptionType = 'warm';
    else if (temperature >= 10) descriptionType = 'mild';
    else if (temperature >= 0) descriptionType = 'cool';
    else if (temperature >= -15) descriptionType = 'cold';
    else descriptionType = 'extreme';

    const descriptions = weatherDescriptions[descriptionType];
    const description = descriptions[Math.floor(Math.random() * descriptions.length)];

    return {
        city: cityName,
        temperature: parseFloat(temperature.toFixed(1)),
        humidity: parseFloat(humidity.toFixed(1)),
        pressure: parseFloat(pressure.toFixed(1)),
        windSpeed: parseFloat(windSpeed.toFixed(1)),
        description: description,
        timestamp: new Date().toISOString()
    };
}

function generateRandomWeather(cityName) {
    const temperature = (Math.random() * 40) - 10;
    const humidity = 30 + Math.random() * 60;
    const pressure = 970 + Math.random() * 60;
    const windSpeed = Math.random() * 15;

    return {
        city: cityName,
        temperature: parseFloat(temperature.toFixed(1)),
        humidity: parseFloat(humidity.toFixed(1)),
        pressure: parseFloat(pressure.toFixed(1)),
        windSpeed: parseFloat(windSpeed.toFixed(1)),
        description: "Standard weather conditions",
        timestamp: new Date().toISOString()
    };
}

function getAvailableCities() {
    return Object.keys(cityWeatherData);
}

function getCityInfo(cityName) {
    return cityWeatherData[cityName] || null;
}

function showCityClimateInfo(city) {
    const info = getCityInfo(city);
    if (info) {
        showNotification(
            `${city}: ${info.description}. Typical temp: ${info.tempRange.min}°C to ${info.tempRange.max}°C`,
            'info'
        );
    }
}

// Manual Input Functions
function toggleManualInput() {
    const strategyType = document.getElementById('strategySelect').value;
    const manualFields = document.getElementById('manualInputFields');

    console.log('Strategy changed to:', strategyType);

    if (strategyType === 'manual') {
        manualFields.style.display = 'block';
        // Set default values
        document.getElementById('manualTemp').value = '20.0';
        document.getElementById('manualHumidity').value = '60';
        document.getElementById('manualPressure').value = '1013';
        document.getElementById('manualWind').value = '3.0';
    } else {
        manualFields.style.display = 'none';
    }
}

async function applyStrategy() {
    const strategyType = document.getElementById('strategySelect').value;
    const city = document.getElementById('cityInput').value.trim() || 'Astana';

    if (!city) {
        showNotification('Please enter a city name', 'error');
        return;
    }

    if (strategyType === 'manual') {
        await applyManualStrategy(city);
    } else {
        await applyAutoStrategy(strategyType, city);
    }
}

async function applyManualStrategy(city) {
    const temp = parseFloat(document.getElementById('manualTemp').value);
    const humidity = parseFloat(document.getElementById('manualHumidity').value);
    const pressure = parseFloat(document.getElementById('manualPressure').value);
    const wind = parseFloat(document.getElementById('manualWind').value);

    console.log('Manual data:', { temp, humidity, pressure, wind });

    // Validate manual input
    if (isNaN(temp) || isNaN(humidity) || isNaN(pressure) || isNaN(wind)) {
        showNotification('Please fill all manual input fields', 'error');
        return;
    }

    if (temp < -50 || temp > 60) {
        showNotification('Please enter valid temperature (-50 to 60°C)', 'error');
        return;
    }
    if (humidity < 0 || humidity > 100) {
        showNotification('Please enter valid humidity (0-100%)', 'error');
        return;
    }
    if (pressure < 800 || pressure > 1100) {
        showNotification('Please enter valid pressure (800-1100 hPa)', 'error');
        return;
    }
    if (wind < 0 || wind > 150) {
        showNotification('Please enter valid wind speed (0-150 m/s)', 'error');
        return;
    }

    try {
        const button = event.target;
        button.classList.add('loading');
        button.disabled = true;

        // First set manual strategy
        const strategyResponse = await fetch(`${API_BASE}/strategy`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ type: 'manual', city: city })
        });

        const strategyResult = await strategyResponse.json();
        console.log('Strategy result:', strategyResult);

        if (strategyResult.status === 'success') {
            // Set manual data
            const manualResponse = await fetch(`${API_BASE}/manual/data`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    city: city,
                    temperature: temp,
                    humidity: humidity,
                    pressure: pressure,
                    windSpeed: wind
                })
            });

            const manualResult = await manualResponse.json();
            console.log('Manual data result:', manualResult);

            if (manualResult.status === 'success') {
                // Update weather with manual data
                await updateWeatherWithManualData();

                updateAppState({
                    currentStrategy: 'Manual Input'
                });

                showNotification('Manual strategy applied with your data!', 'success');
            } else {
                showNotification('Failed to set manual data: ' + manualResult.message, 'error');
            }
        } else {
            showNotification('Failed to set strategy: ' + strategyResult.message, 'error');
        }
    } catch (error) {
        showNotification('Failed to apply manual strategy: ' + error.message, 'error');
        console.error('Manual strategy error:', error);
    } finally {
        const button = event.target;
        button.classList.remove('loading');
        button.disabled = false;
    }
}

async function applyAutoStrategy(strategyType, city) {
    try {
        const button = event.target;
        button.classList.add('loading');
        button.disabled = true;

        const response = await fetch(`${API_BASE}/strategy`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ type: strategyType, city: city })
        });

        const result = await response.json();

        if (result.status === 'success') {
            updateAppState({
                currentStrategy: result.strategyClass.replace('Strategy', '')
            });
            showNotification(`Strategy set to: ${result.strategyClass}`, 'success');
        } else {
            showNotification('Failed to set strategy: ' + result.message, 'error');
        }
    } catch (error) {
        showNotification('Failed to set strategy: ' + error.message, 'error');
    } finally {
        const button = event.target;
        button.classList.remove('loading');
        button.disabled = false;
    }
}

async function updateWeatherWithManualData() {
    try {
        const response = await fetch(`${API_BASE}/update`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        });

        const result = await response.json();

        if (result.status === 'success') {
            displayWeatherData(result.data);
            updateAppState({
                updateCount: appState.updateCount + 1
            });
            showNotification('Weather updated with manual data', 'success');
        }
    } catch (error) {
        showNotification('Failed to update weather: ' + error.message, 'error');
    }
}

// Chart functions
function initializeTemperatureChart() {
    const ctx = document.getElementById('temperatureChart').getContext('2d');

    temperatureChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: temperatureData.labels,
            datasets: [{
                label: 'Temperature (°C)',
                data: temperatureData.temperatures,
                borderColor: '#e74c3c',
                backgroundColor: 'rgba(231, 76, 60, 0.1)',
                borderWidth: 3,
                fill: true,
                tension: 0.4,
                pointBackgroundColor: '#e74c3c',
                pointBorderColor: '#ffffff',
                pointBorderWidth: 2,
                pointRadius: 5,
                pointHoverRadius: 7
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                title: {
                    display: true,
                    text: 'Real-time Temperature Monitoring',
                    font: { size: 16 }
                },
                tooltip: {
                    mode: 'index',
                    intersect: false,
                    callbacks: {
                        label: function(context) {
                            const index = context.dataIndex;
                            const city = temperatureData.cities[index];
                            const time = temperatureData.timestamps[index];
                            return [
                                `City: ${city}`,
                                `Temperature: ${context.parsed.y}°C`,
                                `Time: ${time}`
                            ];
                        }
                    }
                },
                legend: {
                    display: true,
                    position: 'top'
                }
            },
            scales: {
                x: {
                    title: {
                        display: true,
                        text: 'Time'
                    },
                    grid: {
                        color: 'rgba(0,0,0,0.1)'
                    }
                },
                y: {
                    title: {
                        display: true,
                        text: 'Temperature (°C)'
                    },
                    min: -10,
                    max: 40,
                    grid: {
                        color: 'rgba(0,0,0,0.1)'
                    }
                }
            },
            interaction: {
                intersect: false,
                mode: 'nearest'
            },
            animation: {
                duration: 1000,
                easing: 'easeOutQuart'
            }
        }
    });
    loadChartData();
}

function addTemperatureData(city, temperature, timestamp) {
    const selectedCity = document.getElementById('chartCity').value;

    if (selectedCity !== 'All Cities' && city !== selectedCity) {
        return;
    }
    const timeLabel = new Date(timestamp).toLocaleTimeString();

    temperatureData.labels.push(timeLabel);
    temperatureData.temperatures.push(temperature);
    temperatureData.cities.push(city);
    temperatureData.timestamps.push(timestamp);

    const maxDataPoints = 20;
    if (temperatureData.labels.length > maxDataPoints) {
        temperatureData.labels.shift();
        temperatureData.temperatures.shift();
        temperatureData.cities.shift();
        temperatureData.timestamps.shift();
    }

    if (temperatureChart) {
        temperatureChart.data.labels = temperatureData.labels;
        temperatureChart.data.datasets[0].data = temperatureData.temperatures;
        temperatureChart.update('none');
    }

    updateChartStats();
    saveChartData();
}

function updateChartStats() {
    if (temperatureData.temperatures.length === 0) return;

    const temps = temperatureData.temperatures;
    const current = temps[temps.length - 1];
    const avg = temps.reduce((a, b) => a + b, 0) / temps.length;
    const max = Math.max(...temps);
    const min = Math.min(...temps);

    document.getElementById('currentTemp').textContent = `${current.toFixed(1)}°C`;
    document.getElementById('avgTemp').textContent = `${avg.toFixed(1)}°C`;
    document.getElementById('maxTemp').textContent = `${max.toFixed(1)}°C`;
    document.getElementById('minTemp').textContent = `${min.toFixed(1)}°C`;

    const tempElements = {
        current: document.getElementById('currentTemp'),
        avg: document.getElementById('avgTemp'),
        max: document.getElementById('maxTemp'),
        min: document.getElementById('minTemp')
    };

    Object.values(tempElements).forEach(el => {
        const temp = parseFloat(el.textContent);
        el.className = 'stat-number ' + getTempColorClass(temp);
    });
}

function getTempColorClass(temp) {
    if (temp >= 30) return 'temp-high';
    if (temp >= 15) return 'temp-medium';
    return 'temp-low';
}

function clearChart() {
    temperatureData = {
        labels: [],
        temperatures: [],
        cities: [],
        timestamps: []
    };

    if (temperatureChart) {
        temperatureChart.data.labels = [];
        temperatureChart.data.datasets[0].data = [];
        temperatureChart.update();
    }

    updateChartStats();
    localStorage.removeItem('weatherChartData');
    showNotification('Chart cleared', 'info');
}

function saveChartData() {
    try {
        localStorage.setItem('weatherChartData', JSON.stringify(temperatureData));
    } catch (e) {
        console.warn('Could not save chart data to localStorage');
    }
}

function loadChartData() {
    try {
        const saved = localStorage.getItem('weatherChartData');
        if (saved) {
            const data = JSON.parse(saved);
            temperatureData = data;

            if (temperatureChart && data.temperatures.length > 0) {
                temperatureChart.data.labels = data.labels;
                temperatureChart.data.datasets[0].data = data.temperatures;
                temperatureChart.update();
                updateChartStats();
            }
        }
    } catch (e) {
        console.warn('Could not load chart data from localStorage');
    }
}

function filterChartDataByCity(city) {
    if (city === 'All Cities') {
        if (temperatureChart && temperatureData.temperatures.length > 0) {
            temperatureChart.data.labels = temperatureData.labels;
            temperatureChart.data.datasets[0].data = temperatureData.temperatures;
            temperatureChart.update();
        }
    } else {
        const filteredData = {
            labels: [],
            temperatures: [],
            cities: [],
            timestamps: []
        };
        temperatureData.cities.forEach((dataCity, index) => {
            if (dataCity === city) {
                filteredData.labels.push(temperatureData.labels[index]);
                filteredData.temperatures.push(temperatureData.temperatures[index]);
                filteredData.cities.push(temperatureData.cities[index]);
                filteredData.timestamps.push(temperatureData.timestamps[index]);
            }
        });
        if (temperatureChart) {
            temperatureChart.data.labels = filteredData.labels;
            temperatureChart.data.datasets[0].data = filteredData.temperatures;
            temperatureChart.update();
        }
    }
}

// API functions
async function updateWeather() {
    try {
        const button = event.target;
        button.classList.add('loading');

        const response = await fetch(`${API_BASE}/update`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        });
        const result = await response.json();

        if (result.status === 'success') {
            displayWeatherData(result.data);
            updateAppState({
                updateCount: appState.updateCount + 1,
                currentStrategy: result.strategy
            });
            showNotification(`Weather updated using ${result.strategy} strategy`, 'success');
        } else {
            showNotification('Failed to update weather: ' + result.message, 'error');
        }
    } catch (error) {
        showNotification('Failed to update weather: ' + error.message, 'error');
    } finally {
        const button = event.target;
        button.classList.remove('loading');
    }
}

async function createViaFactory() {
    const type = document.getElementById('factoryType').value;
    const city = document.getElementById('factoryCity').value.trim() || 'Almaty';
    try {
        const response = await fetch(`${API_BASE}/factory/create`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({ type, city })
        });

        const result = await response.json();
        document.getElementById('factoryOutput').innerHTML = `
            <div class="pattern-result">
                <div class="info-item">
                    <span class="info-label">Factory:</span>
                    <span class="info-value">${result.factory}</span>
                </div>
                <div class="info-item">
                    <span class="info-label">Created:</span>
                    <span class="info-value">${result.createdStrategy}</span>
                </div>
                <div class="info-item">
                    <span class="info-label">Type:</span>
                    <span class="info-value">${result.type}</span>
                </div>
                <div class="info-item">
                    <span class="info-label">City:</span>
                    <span class="info-value">${result.city}</span>
                </div>
            </div>
        `;
        showNotification('Strategy created via Factory pattern!', 'success');
    } catch (error) {
        showNotification('Factory error: ' + error.message, 'error');
    }
}

async function getFactoryInfo() {
    try {
        const response = await fetch(`${API_BASE}/factory/strategies`);
        const info = await response.json();
        document.getElementById('factoryOutput').innerHTML = `
            <div class="pattern-result">
                <h4>About Factory Pattern</h4>
                <p><strong>Purpose:</strong> ${info.description}</p>
                <p><strong>Method:</strong> <span class="code">${info.factoryMethod}</span></p>
                <p><strong>Available:</strong> ${info.availableStrategies.join(', ')}</p>
            </div>
        `;

    } catch (error) {
        showNotification('Failed to get factory info', 'error');
    }
}

async function showObservers() {
    try {
        const response = await fetch(`${API_BASE}/observers`);
        const info = await response.json();

        updateAppState({ observerCount: info.totalCount });

        document.getElementById('observersOutput').innerHTML = `
            <div class="pattern-result">
                <h4>Observer Pattern Info</h4>
                <p><strong>Active Observers:</strong> ${info.totalCount}</p>
                <p><strong>Observer Types:</strong></p>
                <ul style="margin-left: 20px; margin-top: 8px;">
                    ${info.types.map(type => `<li>${type}</li>`).join('')}
                </ul>
                <p style="margin-top: 12px; font-style: italic;">${info.description}</p>
            </div>
        `;

    } catch (error) {
        showNotification('Failed to get observer info', 'error');
    }
}

async function addObserver() {
    try {
        const response = await fetch(`${API_BASE}/observers/add`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({ type: 'demo' })
        });

        const result = await response.json();
        showNotification('Demo observer added to system', 'success');
        showObservers();

    } catch (error) {
        showNotification('Failed to add observer', 'error');
    }
}

async function checkSingleton() {
    try {
        const response = await fetch(`${API_BASE}/singleton`);
        const info = await response.json();

        document.getElementById('instanceHash').textContent = info.instanceHash;

        document.getElementById('singletonOutput').innerHTML = `
            <div class="pattern-result">
                <h4>Singleton Verification</h4>
                <p><strong>Pattern:</strong> ${info.pattern}</p>
                <p><strong>Implementation:</strong> <span class="code">${info.implementation}</span></p>
                <p><strong>Station:</strong> ${info.stationName}</p>
                <p><strong>Purpose:</strong> ${info.purpose}</p>
                <p style="margin-top: 12px; font-style: italic;">${info.description}</p>
            </div>
        `;

        showNotification('Singleton instance verified!', 'success');

    } catch (error) {
        showNotification('Failed to verify singleton', 'error');
    }
}

function displayWeatherData(data) {
    const weatherDiv = document.getElementById('weatherData');

    if (!data || Object.keys(data).length === 0) {
        weatherDiv.innerHTML = `
            <div class="weather-placeholder">
                <div class="placeholder-icon">🌤️</div>
                <p>No weather data available</p>
                <small>Click "Update Weather" to get data</small>
            </div>
        `;
        return;
    }
    weatherDiv.innerHTML = `
        <div class="weather-data">
            <div class="weather-item">
                <span class="weather-label">📍 City</span>
                <span class="weather-value">${data.city || 'N/A'}</span>
            </div>
            <div class="weather-item">
                <span class="weather-label">🌡️ Temperature</span>
                <span class="weather-value ${getTempColorClass(data.temperature)}">${data.temperature ? data.temperature.toFixed(1) + '°C' : 'N/A'}</span>
            </div>
            <div class="weather-item">
                <span class="weather-label">💧 Humidity</span>
                <span class="weather-value">${data.humidity ? data.humidity.toFixed(0) + '%' : 'N/A'}</span>
            </div>
            <div class="weather-item">
                <span class="weather-label">📊 Pressure</span>
                <span class="weather-value">${data.pressure ? data.pressure.toFixed(0) + ' hPa' : 'N/A'}</span>
            </div>
            <div class="weather-item">
                <span class="weather-label">💨 Wind Speed</span>
                <span class="weather-value">${data.windSpeed ? data.windSpeed.toFixed(1) + ' m/s' : 'N/A'}</span>
            </div>
            <div class="weather-item">
                <span class="weather-label">🕒 Last Update</span>
                <span class="weather-value">${data.timestamp ? new Date(data.timestamp).toLocaleTimeString() : 'N/A'}</span>
            </div>
        </div>
    `;

    if (data.temperature && data.city) {
        addTemperatureData(data.city, data.temperature, data.timestamp);
    }
}

async function getStatus() {
    try {
        const response = await fetch(`${API_BASE}/status`);
        const status = await response.json();

        updateAppState({
            stationName: status.stationName,
            currentStrategy: status.strategy
        });

        if (status.lastData) {
            displayWeatherData(status.lastData);
        }

        showNotification('System status updated', 'success');

    } catch (error) {
        showNotification('Failed to get system status', 'error');
    }
}

async function showAllPatterns() {
    try {
        const response = await fetch(`${API_BASE}/patterns`);
        const patterns = await response.json();

        const patternsHTML = Object.entries(patterns).map(([name, info]) => `
            <div class="pattern-result">
                <h4>${name}</h4>
                <p><strong>Purpose:</strong> ${info.purpose}</p>
                <p><strong>Classes:</strong> <span class="code">${info.classes}</span></p>
                <p><strong>Usage:</strong> ${info.usage}</p>
            </div>
        `).join('');

        document.getElementById('observersOutput').innerHTML = patternsHTML;
        showNotification('All patterns information loaded', 'info');

    } catch (error) {
        showNotification('Failed to load patterns info', 'error');
    }
}

// Initialization
async function initializeApp() {
    try {
        initializeTemperatureChart();

        await getStatus();
        await showObservers();

        // Initialize manual input fields state
        toggleManualInput();

        // Add city info display when city input changes
        document.getElementById('cityInput').addEventListener('change', function() {
            showCityClimateInfo(this.value);
        });

        document.getElementById('chartCity').addEventListener('change', function() {
            const selectedCity = this.value;
            showNotification(`Now tracking temperature for ${selectedCity}`, 'info');
            filterChartDataByCity(selectedCity);
        });

        showNotification('Weather Patterns Demo ready!', 'success');

    } catch (error) {
        showNotification('Failed to initialize app', 'error');
    }
}

document.addEventListener('DOMContentLoaded', initializeApp);

document.addEventListener('keydown', (e) => {
    if (e.ctrlKey || e.metaKey) {
        switch(e.key) {
            case 'r':
                e.preventDefault();
                updateWeather();
                break;
            case 's':
                e.preventDefault();
                getStatus();
                break;
        }
    }
});