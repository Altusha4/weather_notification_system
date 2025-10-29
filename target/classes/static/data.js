// Predefined weather data for demo cities
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

// Weather descriptions based on temperature
const weatherDescriptions = {
    hot: ["Sunny and hot", "Clear skies", "Heat wave", "Dry and sunny"],
    warm: ["Partly cloudy", "Pleasant weather", "Mild conditions", "Clear"],
    mild: ["Cloudy", "Overcast", "Light breeze", "Comfortable"],
    cool: ["Chilly", "Cool breeze", "Mostly cloudy", "Fresh"],
    cold: ["Cold", "Freezing", "Frosty", "Bitter cold"],
    extreme: ["Extreme conditions", "Severe weather", "Storm warning", "Dangerous cold"]
};

// Generate realistic weather data for a city
function generateCityWeather(cityName) {
    const cityData = cityWeatherData[cityName];
    if (!cityData) {
        // Fallback for unknown cities
        return generateRandomWeather(cityName);
    }

    // Generate realistic variations based on city characteristics
    const tempVariation = (Math.random() - 0.5) * 10; // ±5°C variation
    const temperature = cityData.baseTemp + tempVariation;

    const humidityVariation = (Math.random() - 0.5) * 20; // ±10% variation
    const humidity = Math.max(cityData.humidityRange.min,
        Math.min(cityData.humidityRange.max,
            cityData.baseHumidity + humidityVariation));

    const pressureVariation = (Math.random() - 0.5) * 20; // ±10 hPa variation
    const pressure = Math.max(cityData.pressureRange.min,
        Math.min(cityData.pressureRange.max,
            cityData.basePressure + pressureVariation));

    const windVariation = Math.random() * 4; // 0-4 m/s variation
    const windSpeed = Math.max(cityData.windRange.min,
        Math.min(cityData.windRange.max,
            cityData.baseWind + windVariation));

    // Select appropriate description based on temperature
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

// Fallback for unknown cities
function generateRandomWeather(cityName) {
    const temperature = (Math.random() * 40) - 10; // -10°C to 30°C
    const humidity = 30 + Math.random() * 60; // 30% to 90%
    const pressure = 970 + Math.random() * 60; // 970 to 1030 hPa
    const windSpeed = Math.random() * 15; // 0 to 15 m/s

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

// Get all available cities
function getAvailableCities() {
    return Object.keys(cityWeatherData);
}

// Get city information
function getCityInfo(cityName) {
    return cityWeatherData[cityName] || null;
}

// Demo function to show all city data
function displayAllCityData() {
    console.log("=== Predefined City Weather Data ===");
    Object.entries(cityWeatherData).forEach(([city, data]) => {
        console.log(`${city}:`);
        console.log(`  Temperature: ${data.baseTemp}°C (range: ${data.tempRange.min} to ${data.tempRange.max}°C)`);
        console.log(`  Humidity: ${data.baseHumidity}% (range: ${data.humidityRange.min} to ${data.humidityRange.max}%)`);
        console.log(`  Pressure: ${data.basePressure} hPa (range: ${data.pressureRange.min} to ${data.pressureRange.max} hPa)`);
        console.log(`  Wind: ${data.baseWind} m/s (range: ${data.windRange.min} to ${data.windRange.max} m/s)`);
        console.log(`  Description: ${data.description}`);
        console.log('---');
    });
}