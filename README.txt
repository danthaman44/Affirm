The assignment took me 3 hours. This was my first time working in Kotlin, so I had to spend some time brushing up on the syntax. The location client in the starter code didn't work on my emulator, so I mocked out a location provider. I used an MVVM pattern to delegate most of the UI interaction logic to a ViewModel. This has the benefit of keeping the Activity lightweight. This also allows data such as the restaurant list and location to be persisted across configuration changes. The Activity uses LiveData to observe updates to the ViewModel. This keeps the code cleaner and moves it towards a reactive architecture. Below the ViewModel, I implemented two domain layer controllers to separate the core business logic for Yelp and Zomato. Each layer of the application only talks to the layer beneath it - the UI (Activity/ViewPager) only talks to the ViewModel, and the ViewModel only talks to the domain layer. If I had more time, here are some improvements I would make:

1. Add persistent storage using Room to cache restaurant data. The app would then only need to keep the current page of restaurants in memory, while saving previous pages in the cache.
2. Encapsulate data access (Room + Retrofit) in a Repository so the domain layer controllers can retrieve data without worry about the source.
3. Use Kotlin coroutines to parse the Retrofit responses on a background thread. This improves performance by keeping the main thread available for user interaction.
4. Refactor the main Activity to use Fragments. Encapsulating the ViewPager in a Fragment allows it to be reused across multiple Activities.
5. Add a dependency injection library such as Dagger to allow easy access to components such as the LocationProvider, YelpController, and ZomatoController. This becomes important as the app increases in complexity.



