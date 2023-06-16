function getMyBooks() {
    var res = [];
    $.getJSON('http://localhost:8080/api/get?type=bookings', (bookings) => {
        var letterToHour = {"a": "15-16", "b": "16-17", "c": "17-18", "d": "18-19"};
        var numToDay = {0: "Lunedì", 1: "Martedì", 2: "Mercoledì", 3: "Giovedì", 4: "Venerdì"};

        bookings.forEach(book => {
            var statusText
            if (book.status === "booked") {
                statusText = "Prenotata"
            } else if (book.status === "canceled") {
                statusText = "Annullata"
            } else if (book.status === "done") {
                statusText = "Fatta"
            }
            res.push({
                "id": book.id,
                "time": numToDay[book.day] + " alle " + letterToHour[book.hour],
                "hour": book.hour,
                "day": book.day,
                "courseName": book.course.name,
                "teacherSurnameName": book.teacher.name + " " + book.teacher.surname,
                "userSurnameName": book.user.name + " " + book.user.surname,
                "teacherId": book.teacher.id,
                "courseId": book.course.id,
                "status": book.status,
                "statusText": statusText
            })
        })


    });
    return res;
}

export default {
    data: () => ({
        day: 0,
        teaches: getMyBooks(),
        fields: [{
            'key': 'selected',
            'label': 'Selezione',
            'sortable': false
        }, {
            'key': 'time',
            'label': 'Orario',
            'sortable': true
        }, {
            'key': 'time',
            'label': 'Orario',
            'sortable': true
        }, {
            'key': 'courseName',
            'label': 'Corso',
            'sortable': true
        }, {
            'key': 'teacherSurnameName',
            'label': 'Professore',
            'sortable': true
        }, {
            'key': 'statusText',
            'label': 'Stato',
            'sortable': true
        }],
        selected: [],
        isAdmin: null
    }),
    watch: {
        isAdmin: function (val) {
            if (val) {
                this.fields?.splice(1, 0, {
                    'key': 'userSurnameName',
                    'label': 'Utente',
                    'sortable': true
                })
            }
            return val;
        }
    },
    mounted() {
        var self = this
        $.get('/session', function (data) {
            if (data["admin"] === true)
                self.isAdmin = true
            if (data["admin"] === false)
                self.isAdmin = false
        })
    },
    template: `<div id="bookPage" class="myTable">
                    <div id="table">
                        <b-table striped hover :fields="fields" :items="teaches" responsive="sm" selectable
                                 @row-selected="onRowSelected">
                            <template #cell(selected)="{ rowSelected }">
                                <template v-if="rowSelected">
                                    <span aria-hidden="true">&#9745;</span>
                                    <span class="sr-only"></span>
                                </template>
                                <template v-else>
                                    <span aria-hidden="true">&#9744;</span>
                                    <span class="sr-only"></span>
                                </template>
                            </template>
                        </b-table>
                    </div>
                    <div id="Button" class="floatingButtons">
                           <b-button v-on:click="cancel()">🗑️</b-button></br></br>
                           <b-button v-on:click="done()" v-if="!isAdmin">✔️</b-button>
                </div>
                </div>`,

    methods: {
        genTable() {
            this.teaches = getMyBooks();
        }
        ,
        onRowSelected(items) {
            this.selected = items
        }
        ,
        cancel() {
            var self = this
            this.selected.forEach(book => {
                book.status = "canceled"
                $.post('http://localhost:8080/api/set?type=updateStatus',
                    book,
                    function (data) {
                        self.$bvToast.toast('Prenotazione annullata', {
                            title: "Successo",
                            variant: "success",
                            solid: true
                        })
                    },
                    "json"
                ).fail(function (xhr, error_text, statusText) {
                    if (xhr["status"] === 400) {
                        self.$bvToast.toast('Non puoi modificare una prenotazione già annullata', {
                            title: "Errore",
                            variant: "warning",
                            solid: true
                        })
                    } else if (xhr["status"] === 403) {
                        self.$bvToast.toast('Non sei autorizzato ad usare questa funzione', {
                            title: "Non autorizzato",
                            variant: "danger",
                            solid: true
                        })
                    } else if (xhr["status"] === 500) {
                        self.$bvToast.toast('Errore del server', {
                            title: "Non autorizzato",
                            variant: "danger",
                            solid: true
                        })
                    } else {
                        self.$bvToast.toast(xhr["status"], {
                            title: "Errore",
                            variant: "danger",
                            solid: true
                        })
                    }
                }).always(function () {
                    self.teaches = getMyBooks();
                })
            });

        },

        done() {
            var self = this
            this.selected.forEach(book => {
                book.status = "done"
                $.post('http://localhost:8080/api/set?type=updateStatus',
                    book,
                    function (data) {
                        self.$bvToast.toast('Prenotazione segnata come effettuata', {
                            title: "Successo",
                            variant: "success",
                            solid: true
                        })
                    },
                    "json"
                ).fail(function (xhr, error_text, statusText) {
                    if (xhr["status"] === 400) {
                        self.$bvToast.toast('Non puoi modificare una prenotazione già annullata', {
                            title: "Errore",
                            variant: "warning",
                            solid: true
                        })
                    } else if (xhr["status"] === 403) {
                        self.$bvToast.toast('Non sei autorizzato ad usare questa funzione', {
                            title: "Non autorizzato",
                            variant: "danger",
                            solid: true
                        })
                    } else if (xhr["status"] === 500) {
                        self.$bvToast.toast('Errore del server', {
                            title: "Non autorizzato",
                            variant: "danger",
                            solid: true
                        })
                    } else {
                        self.$bvToast.toast(xhr["status"], {
                            title: "Errore",
                            variant: "danger",
                            solid: true
                        })
                    }
                }).always(function () {
                    self.teaches = getMyBooks();
                })
            });
        }
    }
}
