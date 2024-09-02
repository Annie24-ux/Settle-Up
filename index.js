function refreshWeather(response){
    let temperatureElement = document.querySelector("#temperature");
    let temperature = Math.round(response.data.temperature.current);

    let cityElement = document.querySelector("#city");

    cityElement.innerHTML = response.data.city;

    temperatureElement.innerHTML = temperature;

    let descriptionElement = document.querySelector("#description");
    let description = response.data.condition.description;
    descriptionElement.innerHTML = ", " + description;


    let humidElement = document.querySelector("#humid");
    let humid = response.data.temperature.humidity;
    humidElement.innerHTML = `${humid} %`;

    let windElement = document.querySelector("#wind");
    let wind = response.data.wind.speed;
    windElement.innerHTML = `${wind}  km/h`;

    let timeElement = document.querySelector("#time");
    let weatherDate = new Date(response.data.time * 1000);
    timeElement.innerHTML = formatDate(weatherDate);

    let iconElement = document.querySelector("#weather-icon");
    iconElement.innerHTML = `<img src="${response.data.condition.icon_url}" class="weather-icon" />`;
    // let icon = response.data.condition.icon;
    // iconElement.innerHTML = icon;

    function formatDate(date){

        let weekDays = ["Sunday","Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];

        let hours = date.getHours();
        let minutes = date.getMinutes();
        let day = weekDays[date.getDay()];

        if(minutes < 10){
            minutes = `0${minutes}`;
        }

        if(hours < 10){
            hours = `0${hours}`;
        }

        return `${day} ${hours}:${minutes}`;




    }
}



function citySearch(city){
    let apiKey = "b2a5adcct04b33178913oc335f405433";
    let weatherUrl = `https://api.shecodes.io/weather/v1/current?query=${city}&key=${apiKey}&units=metric`;
    console.log(weatherUrl);

    axios.get(weatherUrl).then(refreshWeather);
}


function searchSubmitHandler(event){
    event.preventDefault();
    let searchInput = document.querySelector("#form-input");

    citySearch(searchInput.value);
}





let searchFormElement = document.querySelector("#answer");
searchFormElement.addEventListener("click", searchSubmitHandler);

citySearch("Malelane");
