export default {
    data: () => ({
        form: {
            username: '',
            password: ''
        }
    }),
    template: `
<div id="login">

<b-form @submit="onSubmit" @reset="onReset">
                <b-form-group
                        id="input-group-1"
                        label="Username: "
                        label-for="input-1"
                >
                    <b-form-input
                            id="input-1"
                            v-model="form.username"
                            placeholder="Username"
                            required
                    ></b-form-input>
                </b-form-group>

                <b-form-group id="input-group-2" label="Password: " label-for="input-2">
                    <b-form-input
                            id="input-2"
                            v-model="form.password"
                            placeholder="Password"
                            type="password"
                            required
                    ></b-form-input>
                </b-form-group>

                <b-button type="submit" variant="primary">Submit</b-button>
                <b-button type="reset" variant="danger">Reset</b-button>
            </b-form>
</div>`,
    created: function () {
        var self = this
        $.get('/session', function (data) {
            self.$router.push("/");
        });
    },
    methods: {
        onSubmit(event) {
            event.preventDefault()
            var self = this
            $.post('/session', this.form, function (data) {
                self.$router.push("/");
            }).fail(function (xhr, error_text, statusText) {
                    if (xhr["status"] === 403) {
                        self.$bvToast.toast('Username e/o password errati', {
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
                }
            )

        },
        onReset(event) {
            event.preventDefault()
            this.form.username = ''
            this.form.password = ''
        }
    }
}