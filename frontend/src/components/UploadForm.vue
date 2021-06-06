<template>
  <v-dialog @click:outside="closeDialog" :value="dialog" max-width="1000px">
    <v-card @drop.prevent="onDrop($event)"
            @dragover.prevent="dragover = true"
            @dragenter.prevent="dragover = true"
            @dragleave.prevent="dragover = false"
            :class="{ 'blue lighten-4': dragover }"
            class="pa-16" min-height="400px">
      <v-card-text class="align-center pa-16" v-if="files.length === 0">
        <v-row>
          <v-col cols="12" class="d-flex justify-center align-center">
            <v-icon size="60">
              mdi-file-multiple
            </v-icon>
          </v-col>
          <v-col cols="12" class="d-flex justify-center align-center">
            Drag and drop your attachment(s) here.
          </v-col>
        </v-row>
      </v-card-text>
      <v-list class="align-center" v-else-if="files.length > 0" flat min-height="200px">
        <v-list-group v-for="(file, index) in files" :key="index">
          <template v-slot:activator>
            <v-list-item-action>
              <v-btn @click="removeFile(index)" icon>
                <v-icon> mdi-close-circle</v-icon>
              </v-btn>
            </v-list-item-action>
            <v-list-item-content>
              <v-row>
                <v-col>
                  {{ file.name }}
                </v-col>
                <v-col cols="3" class="text-center text--secondary">
                  {{ file.size }} bytes
                </v-col>
              </v-row>
              <v-divider></v-divider>
            </v-list-item-content>
          </template>
          <v-img :src="src[index]" :alt="file.name" contain class="ma-4">
          </v-img>
        </v-list-group>
      </v-list>
      <v-card-text class="justify-end">
        <v-row>
          <v-col>
            <v-file-input
                v-model="files"
                multiple
                show-size
                label="Select Files"
            ></v-file-input>
          </v-col>
          <v-btn :loading="loading" :disabled="loading"
                 class="ma-2 white--text text-right" elevation="3" rounded color="teal"
                 @click="upload">
            Upload
            <v-icon right dark>mdi-cloud-upload</v-icon>
          </v-btn>
        </v-row>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script>
export default {
  name: "UploadForm",
  data() {
    return {
      dragover: false,
      files: [],
      src: [],
      loading: false,
    };
  },
  props: {
    dialog: {
      type: Boolean,
      required: true
    },
    projectId: String,
    issueId: Number
  },
  methods: {
    closeDialog() {
      this.files = [];
      this.$emit("update:dialog", false);
    },
    onDrop(e) {
      this.dragover = false;
      for (let i = 0; i < e.dataTransfer.files.length; i++) {
        this.files.push(e.dataTransfer.files[i])
      }
      this.getImagePreviews()
    },
    upload() {
      if (this.files.length !== 0) {
        this.loading = true;
        let formData = new FormData();
        if (this.issueId != null) {
          formData.append("parent", "issue");
          formData.append("id", this.issueId.toString());
        } else {
          formData.append("parent", "project");
          formData.append("id", this.projectId);
        }
        for (let i = 0; i < this.files.length; i++) {
          formData.append("files", this.files[i]);
        }
        return fetch(`/api/upload`, {
          method: 'POST',
          body: formData
        }).then((res) => {
          if (res.status === 200) {
            console.log("Uploaded the attachment")
            this.$emit("update-attachment")
            this.closeDialog()
          } else {
            alert("Cannot upload the attachment")
          }
        }).catch((e) => console.log(e))
            .finally(() => this.loading = false)
      }
    },
    removeFile(key) {
      this.files.splice(key, 1);
      this.getImagePreviews()
    },
    getImagePreviews() {
      this.src = [];
      for (let i = 0; i < this.files.length; i++) {
        if (/\.(jpe?g|png|gif)$/i.test(this.files[i].name)) {
          let reader = new FileReader();
          reader.addEventListener("load", function () {
            this.src.push(reader.result);
          }.bind(this), false);
          reader.readAsDataURL(this.files[i]);
        } else {
          this.$nextTick(function () {
            this.$refs['preview' + parseInt(i)][0].src = '/images/attachment.png';
          });
        }
      }
    },
  },
};
</script>

<style scoped>

</style>