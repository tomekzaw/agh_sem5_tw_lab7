// Teoria Współbieżnośi, implementacja problemu 5 filozofów w node.js
// Opis problemu: http://en.wikipedia.org/wiki/Dining_philosophers_problem
//   https://pl.wikipedia.org/wiki/Problem_ucztuj%C4%85cych_filozof%C3%B3w
// 1. Dokończ implementację funkcji podnoszenia widelca (Fork.acquire).
// 2. Zaimplementuj "naiwny" algorytm (każdy filozof podnosi najpierw lewy, potem
//    prawy widelec, itd.).
// 3. Zaimplementuj rozwiązanie asymetryczne: filozofowie z nieparzystym numerem
//    najpierw podnoszą widelec lewy, z parzystym -- prawy. 
// 4. Zaimplementuj rozwiązanie z kelnerem (według polskiej wersji strony)
// 5. Zaimplementuj rozwiążanie z jednoczesnym podnoszeniem widelców:
//    filozof albo podnosi jednocześnie oba widelce, albo żadnego.
// 6. Uruchom eksperymenty dla różnej liczby filozofów i dla każdego wariantu
//    implementacji zmierz średni czas oczekiwania każdego filozofa na dostęp 
//    do widelców. Wyniki przedstaw na wykresach.

async = require("async");

calcTimeDiff = function(startTime, endTime) {
    return (endTime[0]-startTime[0])*1e9 + (endTime[1]-startTime[1]);
}

var Fork = function(id) {
    this.id = id;
    this.state = 0;
    return this;
}

Fork.prototype.acquire = function(cb) { 
    // zaimplementuj funkcję acquire, tak by korzystala z algorytmu BEB
    // (http://pl.wikipedia.org/wiki/Binary_Exponential_Backoff), tzn:
    // 1. przed pierwszą próbą podniesienia widelca Filozof odczekuje 1ms
    // 2. gdy próba jest nieudana, zwiększa czas oczekiwania dwukrotnie
    //    i ponawia próbę, itd.
    var func = function(fork, delay, cb) {
        setTimeout(function() {
            if (fork.state) {
                func(fork, 2*delay, cb);
            } else {
                fork.state = 1;
                if (cb) cb();
            }
        }, delay);
    }
    startTime = process.hrtime();
    func(this, 1, cb);
}

Fork.prototype.release = function() { 
    this.state = 0;
}

var Philosopher = function(id, forks) {
    this.id = id;
    this.forks = forks;
    this.f1 = id % forks.length;
    this.f2 = (id+1) % forks.length;
    this.measurements = [];
    return this;
}

Philosopher.prototype.startNaive = function(count) {
    var forks = this.forks,
        f1 = this.f1,
        f2 = this.f2,
        id = this.id;
    
    // zaimplementuj rozwiązanie naiwne
    // każdy filozof powinien 'count' razy wykonywać cykl
    // podnoszenia widelców -- jedzenia -- zwalniania widelców

    var left = forks[f1], right = forks[f2];
    var todo = function(cb) {
        left.acquire(function() {
            right.acquire(function() {
                console.error(id);
                setTimeout(function() {
                    left.release();
                    right.release();
                    setTimeout(function() {
                        if (cb) cb();
                    }, Math.random()*10);
                }, Math.random()*10);
            });
        });
    }
    async.waterfall(Array(count).fill(todo));
}

Philosopher.prototype.startAsym = function(count) {
    var forks = this.forks,
        f1 = this.f1,
        f2 = this.f2,
        id = this.id,
        measurements = this.measurements;
    
    // zaimplementuj rozwiązanie asymetryczne
    // każdy filozof powinien 'count' razy wykonywać cykl
    // podnoszenia widelców -- jedzenia -- zwalniania widelców

    var left = forks[f1], right = forks[f2];
    if (id % 2) {
        var first = left, second = right;
    } else {
        var first = right, second = left;
    }
    var todo = function(cb) {
        startTime = process.hrtime();
        first.acquire(function() {
            second.acquire(function() {
                endTime = process.hrtime();
                measurements.push(calcTimeDiff(startTime, endTime));
                console.error(id);
                setTimeout(function() {
                    first.release();
                    second.release();
                    setTimeout(function() {
                        if (cb) cb();
                    }, Math.random()*10);
                }, Math.random()*10);
            });
        });
    }
    async.waterfall(Array(count).fill(todo), function() {
        console.log(measurements.join(';'));
    });
}

var Conductor = function(N) {
    this.state = N-1;
    return this;
}

Conductor.prototype.acquire = function(cb) { 
    var func = function(conductor, delay, cb) {
        setTimeout(function() {
            if (conductor.state > 0) {
                --conductor.state;
                if (cb) cb();
            } else {
                func(conductor, 2*delay, cb);
            }
        }, delay);
    }
    func(this, 1, cb);
}

Conductor.prototype.release = function() { 
    ++this.state;
}

Philosopher.prototype.startSimultaneous = function(count) {
    var forks = this.forks,
        f1 = this.f1,
        f2 = this.f2,
        id = this.id,
        measurements = this.measurements;

    // TODO: wersja z jednoczesnym podnoszeniem widelców
    // Algorytm BEB powinien obejmować podnoszenie obu widelców, 
    // a nie każdego z osobna

    var left = forks[f1], right = forks[f2];
    var startTime, endTime;
    var todo = function(cb) {
        startTime = process.hrtime();
        var func = function(delay, cb) {
            setTimeout(function() {
                if (left.state || right.state) {
                    func(2*delay, cb);
                } else {
                    endTime = process.hrtime();
                    measurements.push(calcTimeDiff(startTime, endTime));
                    left.state = right.state = 1;
                    console.error(id);
                    setTimeout(function() {
                        left.state = right.state = 0;
                        setTimeout(function() {
                            if (cb) cb();
                        }, Math.random()*10);
                    }, Math.random()*10);
                }
            }, delay);
        }
        func(1, cb);
    }
    async.waterfall(Array(count).fill(todo), function() {
        console.log(measurements.join(';'));
    });
}

Philosopher.prototype.startConductor = function(count) {
    var forks = this.forks,
        f1 = this.f1,
        f2 = this.f2,
        id = this.id,
        measurements = this.measurements;
    
    // zaimplementuj rozwiązanie z kelnerem
    // każdy filozof powinien 'count' razy wykonywać cykl
    // podnoszenia widelców -- jedzenia -- zwalniania widelców

    var left = forks[f1], right = forks[f2];
    var todo = function(cb) {
        startTime = process.hrtime();
        conductor.acquire(function() {
            left.acquire(function() {
                right.acquire(function() {
                    endTime = process.hrtime();
                    measurements.push(calcTimeDiff(startTime, endTime));
                    console.error(id);
                    setTimeout(function() {
                        left.release();
                        right.release();
                        conductor.release();
                        setTimeout(function() {
                            if (cb) cb();
                        }, Math.random()*10);
                    }, Math.random()*10);
                });
            });
        });
    }
    async.waterfall(Array(count).fill(todo), function() {
        console.log(measurements.join(';'));
    });
}


var N = 5;
var forks = [];
var philosophers = [];
for (var i = 0; i < N; i++) {
    forks.push(new Fork(i));
}

for (var i = 0; i < N; i++) {
    philosophers.push(new Philosopher(i, forks));
}

conductor = new Conductor(N);

for (var i = 0; i < N; i++) {
    // philosophers[i].startNaive(1000); // naive
    philosophers[i].startAsym(1000); // asymmetric
    // philosophers[i].startSimultaneous(1000); // simultaneous
    // philosophers[i].startConductor(1000); // waiter
}