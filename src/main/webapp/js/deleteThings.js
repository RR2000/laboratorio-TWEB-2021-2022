function getTeachers() {
    var res = []
    $.getJSON('http://localhost:8080/api/get?type=teachers', (teachers) => {
        teachers.forEach(teacher => {
            res.push(
                {
                    'text': teacher["surname"] + " " + teacher["name"],
                    'value': {'id': teacher["id"]}
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
                    'value': {'id': course["id"]}
                }
            )
        });
    });
    return res;
}


function getTeaches() {
    var res = []
    $.getJSON('http://localhost:8080/api/get?type=teaches', (teaches) => {
        teaches.forEach(teach => {
            res.push(
                {
                    'text': teach.teacher.name + " " + teach.teacher.surname + " - " + teach.course.name,
                    'value': {'teacherId': teach.teacher.id, 'courseId': teach.course.id}
                }
            )
        });
    });
    return res;
}

export default {
    data: () => ({

        optionsTeachers: getTeachers(),
        optionsCourses: getCourses(),
        optionsTeaches: getTeaches(),
        selectedCourse: null,
        selectedTeacher: null,
        selectedTeach: null
    }),
    template: ` <div id="removeThings" class="myInputs">
                    <div id="removeCourse">
                        <h2>Rimuovi corso</h2>
                        <b-form @submit="onSubmitCourse">
                            <b-form-select v-model="selectedCourse" :options="optionsCourses" required></b-form-select>
                            </br></br>
                            <b-button type="submit" class="myButton">Rimuovi corso</b-button>
                        </b-form>
                    </div>
                    </br></br>
                    <div id="removeTeacher">
                        <h2>Rimuovi docente</h2>
                        <b-form @submit="onSubmitTeacher">
                            <b-form-select v-model="selectedTeacher" :options="optionsTeachers" required></b-form-select>
                            </br></br>
                            <b-button type="submit" class="myButton">Rimuovi Professore</b-button>
                        </b-form>
                    </div>
                    </br></br>
                    <div id="removeTeach">
                        <h2>Rimuovi insegnamento</h2>
                        <b-form @submit="onSubmitTeach">
                            <b-form-select v-model="selectedTeach" :options="optionsTeaches" required></b-form-select>
                            </br></br>
                            <b-button type="submit" class="myButton">Rimuovi insegnamento</b-button>
                        </b-form>
                    </div>
                </div>`,

    methods: {
        onSubmitCourse(event) {
            event.preventDefault()
            var self = this
            $.post('http://localhost:8080/api/delete?type=course',
                self.selectedCourse,
                function (data) {
                    self.$bvToast.toast("Corso rimosso con successo", {
                        title: "Successo",
                        variant: "success",
                        solid: true
                    })
                    self.optionsCourses = getCourses()
                    self.selectedCourse = null
                },
                "json"
            ).fail(function (xhr, error_text, statusText) {
                if (xhr["status"] === 403) {
                    self.$bvToast.toast('Non sei autorizzato a rimuovere un corso', {
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
            $.post('http://localhost:8080/api/delete?type=teacher',
                self.selectedTeacher,
                function (data) {
                    self.$bvToast.toast("Professore rimosso con successo", {
                        title: "Successo",
                        variant: "success",
                        solid: true
                    })
                    self.optionsTeachers = getTeachers()
                    self.selectedTeacher = null
                },
                "json"
            ).fail(function (xhr, error_text, statusText) {
                if (xhr["status"] === 403) {
                    self.$bvToast.toast('Non sei autorizzato a rimuovere un professore', {
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
            $.post('http://localhost:8080/api/delete?type=teach',
                self.selectedTeach,
                function (data) {
                    self.$bvToast.toast("Insegnamento rimosso con successo", {
                        title: "Successo",
                        variant: "success",
                        solid: true
                    })
                    self.optionsTeaches = getTeaches()
                    self.optionsTeaches = null
                },
                "json"
            ).fail(function (xhr, error_text, statusText) {
                if (xhr["status"] === 403) {
                    self.$bvToast.toast('Non sei autorizzato a rimuovere un insegnamento', {
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
        }
    }
}
