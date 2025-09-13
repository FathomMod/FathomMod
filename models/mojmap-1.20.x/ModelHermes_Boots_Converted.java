// Made with Blockbench 4.10.2
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

public class ModelHermes_Boots_Converted<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation("modid", "hermes_boots_converted"), "main");
	private final ModelPart Boot;
	private final ModelPart Booot;

	public ModelHermes_Boots_Converted(ModelPart root) {
		this.Boot = root.getChild("Boot");
		this.Booot = root.getChild("Booot");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Boot = partdefinition.addOrReplaceChild("Boot",
				CubeListBuilder.create().texOffs(5, 3)
						.addBox(-1.5F, 12.45F, -2.25F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(12, 0)
						.addBox(-2.5F, 10.45F, -3.0F, 5.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 12)
						.addBox(-2.5F, 8.45F, -2.5F, 5.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(6, 4)
						.addBox(1.5001F, 8.45F, -1.25F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 15)
						.addBox(1.5F, 8.45F, -2.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(12, 12)
						.addBox(-2.5F, 8.45F, -2.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
						.addBox(-2.5001F, 8.45F, -1.25F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(12, 3)
						.addBox(-1.5F, 8.45F, 1.75F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(3, 0)
						.addBox(-1.5F, 12.45F, -1.25F, 3.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)),
				PartPose.offset(2.0F, 11.55F, -0.25F));

		PartDefinition Booot = partdefinition.addOrReplaceChild("Booot",
				CubeListBuilder.create().texOffs(12, 0)
						.addBox(-2.55F, 10.45F, -3.0F, 5.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 12)
						.addBox(-2.55F, 8.45F, -2.5F, 5.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(6, 4)
						.addBox(1.4501F, 8.45F, -1.25F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 15)
						.addBox(1.45F, 8.45F, -2.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(12, 12)
						.addBox(-2.55F, 8.45F, -2.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
						.addBox(-2.5501F, 8.45F, -1.25F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(12, 3)
						.addBox(-1.55F, 8.45F, 1.75F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(3, 0)
						.addBox(-1.55F, 12.45F, -1.25F, 3.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)).texOffs(5, 3)
						.addBox(-1.55F, 12.45F, -2.25F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offset(-2.8F, 11.55F, -0.25F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha) {
		Boot.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		Booot.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}