function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}


function getJSONday(dayNum) {
    var res = [];
    $.getJSON('http://localhost:8080/api/get?type=teachesOfDay&day=' + dayNum, (day) => {
        var letterToHour = {"a": "15-16", "b": "16-17", "c": "17-18", "d": "18-19"};

        Object.keys(day).forEach(hourLetter =>
            day[hourLetter].forEach(teach =>
                res.push({
                    "time": letterToHour[hourLetter],
                    "hour": hourLetter,
                    "day": dayNum,
                    "courseName": teach.course.name,
                    "teacherSurnameName": teach.teacher.surname + " " + teach.teacher.name,
                    "teacherId": teach.teacher.id,
                    "courseId": teach.course.id
                })
            )
        );
    });
    return res;
}

export default {
    data: () => ({
        day: 0,
        isBookable: Boolean,
        fields: [
            {
                'key': 'time',
                'label': 'Orario',
                'sortable': true
            },
            {
                'key': 'courseName',
                'label': 'Corso',
                'sortable': true
            },
            {
                'key': 'teacherSurnameName',
                'label': 'Professore',
                'sortable': true
            },

        ],
        teaches: getJSONday(0),
        pippo: "selectable",
        selected: []
    }),
    template: `<div id="bookPage" class="myTable">
                    <div id="buttons" class="myButtonsGroup">
                        <b-button-toolbar key-nav>
                            <b-button-group class="mx-1">
                                <b-button v-on:click="genTable(0)" :pressed="day === 0">Lunedì</b-button>
                                <b-button v-on:click="genTable(1)" :pressed="day === 1">Martedì</b-button>
                                <b-button v-on:click="genTable(2)" :pressed="day === 2">Mercoledì</b-button>
                                <b-button v-on:click="genTable(3)" :pressed="day === 3">Giovedì</b-button>
                                <b-button v-on:click="genTable(4)" :pressed="day === 4">Venerdì</b-button>
                            </b-button-group>
                        </b-button-toolbar>
                    </div>
                    <div id="table">
                        <b-table v-if="isBookable" striped hover selectable :fields="fields" :items="teaches" 
                        responsive="sm" 
                                 @row-selected="onRowSelected">
                            <template #cell(selected)="{ rowSelected }" >
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
                        <b-table v-else striped hover :fields="fields" :items="teaches" responsive="sm" >
                           
                        </b-table>
                    </div>
                    <div id="bookButton" v-if="isBookable" class="floatingButtons">
                           <b-button v-on:click="book()">➕</b-button>
                    </div>
                </div>`,
    watch: {
        isBookable: function (val) {
            if (val) {
                this.fields?.splice(0, 0, {
                    'key': 'selected',
                    'label': 'Prenota',
                    'sortable': false
                })
            }
            return val;
        }
    },
    mounted() {
        var self = this
        $.get('/session', function (data) {
            self.isBookable = !data["admin"];
        }).fail(function (xhr, error_text, statusText) {
            self.isBookable = false;
        })
    },
    methods: {
        genTable(dayNum) {
            this.teaches = getJSONday(dayNum);
            this.day = dayNum;
        },
        onRowSelected(items) {
            this.selected = items
        },
        async book() {

            var self = this
            var letterToHour = {"a": "15-16", "b": "16-17", "c": "17-18", "d": "18-19"};
            var numToDay = ["Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì"]
            for (const book of this.selected) {
                await sleep(100);
                var hourDayText = "alle ore " + letterToHour[book.hour] + " di " + numToDay[book.day]
                $.post('http://localhost:8080/api/set?type=book',
                    book,
                    function (data) {
                        self.$bvToast.toast('Ripetizione prenotata con successo ' + hourDayText, {
                            title: "Successo",
                            variant: "success",
                            solid: true
                        })
                    },
                    "json"
                ).fail(function (xhr, error_text, statusText) {
                    if (xhr["status"] === 403) {
                        self.$bvToast.toast('Non sei autorizzato a prenotarti ' + hourDayText, {
                            title: "Non autorizzato",
                            variant: "danger",
                            solid: true
                        })
                    } else if (xhr["status"] === 409) {
                        self.$bvToast.toast('Professore o utente già impegnato ' + hourDayText, {
                            title: "Prenotazione non aggiunta",
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
                })

            }

            this.teaches = getJSONday(this.day);
        }
    }
}
