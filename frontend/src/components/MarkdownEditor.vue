<template>
<div>
    <v-container>
        <v-btn text @click="preview = !preview">{{preview ? "Close" : ''}} Preview</v-btn>
    </v-container>
    <v-textarea solo v-model="markdown" v-show="!preview" :placeholder="placeholder" autofocus></v-textarea>
    <v-container v-show="preview">
        <div v-html="html"></div>
    </v-container>
</div>
</template>

<script>
import showdown from "showdown";
// import TurndownService from 'turndown';
export default {
    name: 'MarkdownEditor',
    data() {
        return {
            markdown: '',
            html: '',
            converter: new showdown.Converter({strikethrough: true, emoji: true}),
            preview: false
        }
    },
    watch: {
        markdown(val) {
            console.log("run");
            this.html = this.converter.makeHtml(val);
            this.$emit('input', this.html);
        },
    },
    props: {
        value: String,
        placeholder: String
    },
    created() {
        this.markdown = this.converter.makeMarkdown(this.value);
        console.log(this.converter.getFlavor());
    }
}
</script>