function getTeachers() {
    var res = []
    $.getJSON('http://localhost:8080/api/get?type=teachers', (teachers) => {
        teachers.forEach(teacher => {
            res.push(
                {
                    'text': teacher["surname"] + " " + teacher["name"],
                    'value': teacher["id"]
                }
            )
        });
    });
    return res;
}

function getCourses() {
    var res = []
    $.getJSON('http://localhost:8080/api/get?type=courses', (courses) => {
        courses.forEach(course => {
            res.push(
                {
                    'text': course["name"],
                    'value': course["id"]
                }
            )
        });
    });
    return res;
}

export default {
    data: () => ({
        courseForm: {
            name: '',
            description: ''
        },
        teacherForm: {
            name: '',
            surname: ''
        },
        optionsTeachers: getTeachers(),
        optionsCourses: getCourses(),
        selectedTeach: {
            courseId: null,
            teacherId: null,
        }
    }),
    template: ` <div id="addThings" class="myInputs">
                    <div id="addCourse">
                        <h2>Aggiungi corso</h2>
                        <b-form @submit="onSubmitCourse">
                            <b-form-group
                                    id="input-group-1"
                                    label="Nome del corso:"
                                    label-for="input-1"
                            >
                                <b-form-input
                                        id="input-1"
                                        v-model="courseForm.name"
                                        placeholder="Inserisci nome corso"
                                        required
                                ></b-form-input>
                            </b-form-group>

                            <b-form-group 
                                    id="input-group-2"
                                    label="Descrizione del corso:"
                                    label-for="input-2"
                            >
                                <b-form-input
                                        id="input-2"
                                        v-model="courseForm.description"
                                        placeholder="Inserisci descrizione del corso"
                                        required
                                ></b-form-input>
                            </b-form-group>

                            <b-button type="submit"  class="myButton">Aggiungi corso</b-button>
                        </b-form>
                    </div>
                    </br></br>
                    <div id="addTeacher">
                        <h2>Aggiungi professore</h2>
                        <b-form @submit="onSubmitTeacher">
                            <b-form-group
                                    id="input-group-1"
                                    label="Nome del professore:"
                                    label-for="input-1"
                            >
                                <b-form-input
                                        id="input-1"
                                        v-model="teacherForm.name"
                                        placeholder="Inserisci nome del professore"
                                        required
                                ></b-form-input>
                            </b-form-group>

                            <b-form-group 
                                    id="input-group-2"
                                    label="Cognome del professore:"
                                    label-for="input-2"
                            >
                                <b-form-input
                                        id="input-2"
                                        v-model="teacherForm.surname"
                                        placeholder="Inserisci cognome del professore"
                                        required
                                ></b-form-input>
                            </b-form-group>

                            <b-button type="submit"  class="myButton">Aggiungi professore</b-button>
                        </b-form>
                    </div>
                    </br></br>
                    <div id="addTeach">
                        <h2>Aggiungi insegnamento</h2>
                        <b-form @submit="onSubmitTeach">
                            Scegli professore: <b-form-select v-model="selectedTeach.teacherId" :options="optionsTeachers" required></b-form-select>
                            </br></br>
                            Scegli corso: <b-form-select v-model="selectedTeach.courseId" :options="optionsCourses" required></b-form-select>
                            </br></br>
                            <b-button type="submit"  class="myButton">Aggiungi insegnamento</b-button>
                        </b-form>
                    </div>
                </div>`,

    methods: {
        onSubmitCourse(event) {
            event.preventDefault()
            var self = this
            $.post('http://localhost:8080/api/set?type=course',
                self.courseForm,
                function (data) {
                    self.$bvToast.toast("Corso aggiunto con successo", {
                        title: "Successo",
                        variant: "success",
                        solid: true
                    })
                    self.optionsCourses = getCourses()
                    self.courseForm.name = ""
                    self.courseForm.description = ""
                },
                "json"
            ).fail(function (xhr, error_text, statusText) {
                if (xhr["status"] === 403) {
                    self.$bvToast.toast('Non sei autorizzato ad aggiungere un corso', {
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
            })

        },
        onSubmitTeacher(event) {
            event.preventDefault()
            var self = this
            $.post('http://localhost:8080/api/set?type=teacher',
                self.teacherForm,
                function (data) {
                    self.$bvToast.toast("Professore aggiunto con successo", {
                        title: "Successo",
                        variant: "success",
                        solid: true
                    })
                    self.optionsTeachers = getTeachers()
                    self.teacherForm.name = ""
                    self.teacherForm.surname = ""
                },
                "json"
            ).fail(function (xhr, error_text, statusText) {
                if (xhr["status"] === 403) {
                    self.$bvToast.toast('Non sei autorizzato ad aggiungere un professore', {
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
            })
        },
        onSubmitTeach(event) {
            event.preventDefault()
            var self = this
            $.post('http://localhost:8080/api/set?type=teach',
                self.selectedTeach,
                function (data) {
                    self.$bvToast.toast("Insegnamento aggiunto con successo", {
                        title: "Successo",
                        variant: "success",
                        solid: true
                    })
                    self.optionsTeachers = getTeachers()
                    self.optionsCourses = getCourses()
                },
                "json"
            ).fail(function (xhr, error_text, statusText) {
                if (xhr["status"] === 403) {
                    self.$bvToast.toast('Non sei autorizzato ad aggiungere un insegnamento', {
                        title: "Non autorizzato",
                        variant: "danger",
                        solid: true
                    })
                } else if (xhr["status"] === 409) {
                    self.$bvToast.toast('Hai tentato di aggiungere un insegnamento già presente ne server', {
                        title: "Conflitto",
                        variant: "warning",
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
    }
}
