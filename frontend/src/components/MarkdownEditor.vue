<template>
<div>
    <div>
        <v-btn icon @click="preview = !preview">
            <v-icon :color="preview ? 'primary': undefined">mdi-eye-outline</v-icon>
        </v-btn>
    </div>
    <v-textarea solo flat v-model="markdown" v-show="!preview" :placeholder="placeholder" autofocus></v-textarea>
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
            
            this.html = this.converter.makeHtml(val);
            this.$emit('input', this.html);
        },
        value(val) {
            if (val.length > 0) return;
            this.markdown = '';
        }
    },
    props: {
        value: String,
        placeholder: String
    },
    created() {
        this.markdown = this.converter.makeMarkdown(this.value);
        
    }
}
</script>